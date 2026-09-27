package com.example.boilerplate.service;

import com.example.boilerplate.security.JwtService;
import com.example.boilerplate.model.RefreshToken;
import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.RefreshTokenRepository;
import com.example.boilerplate.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.boilerplate.config.JwtProperties;
import com.example.boilerplate.auth.dto.AuthTokensResponse;
import com.example.boilerplate.auth.dto.LoginRequest;
import com.example.boilerplate.auth.dto.RegisterRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public User register(String email, String rawPassword, String fullName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        user = userRepository.save(user);
        return user;
    }

    public String loginAccessToken(User user, long ttlMillis) {
        return jwtService.generateToken(user.getId().toString(), ttlMillis);
    }

    @Transactional
    public String createRefreshToken(User user, long ttlDays) throws Exception {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) { hex.append(String.format("%02x", b)); }
        String tokenHash = hex.toString();
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setTokenHash(tokenHash);
        rt.setExpiresAt(Instant.now().plus(ttlDays, ChronoUnit.DAYS));
        refreshTokenRepository.save(rt);
        return token;
    }

    public User authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

    @Transactional(readOnly = true)
    public User validateRefreshToken(String token) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) { hex.append(String.format("%02x", b)); }
        String tokenHash = hex.toString();
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));
    }

    @Transactional
    public String rotateRefreshToken(String oldToken, long ttlDays) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(oldToken.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) { hex.append(String.format("%02x", b)); }
        String tokenHash = hex.toString();
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        User user = existing.getUser();
        // delete old token
        refreshTokenRepository.delete(existing);
        // create new token
        return createRefreshToken(user, ttlDays);
    }

    @Transactional
    public void revokeRefreshToken(String token) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) { hex.append(String.format("%02x", b)); }
        String tokenHash = hex.toString();
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(rt -> refreshTokenRepository.delete(rt));
    }

    // Adapter methods for controller DTOs
    public AuthTokensResponse register(RegisterRequest r) throws Exception {
        User user = register(r.email(), r.password(), null);
        long refreshDays = parseDaysFromTtl(jwtProperties.getRefreshTokenTtl());
        String refreshPlain = createRefreshToken(user, refreshDays);
        long accessSecs = parseSeconds(jwtProperties.getAccessTokenTtl());
        String access = loginAccessToken(user, accessSecs * 1000L);
        return new AuthTokensResponse(access, "Bearer", accessSecs, refreshPlain, refreshDays * 24 * 3600L);
    }

    public AuthTokensResponse login(LoginRequest r) throws Exception {
        User user = authenticate(r.email(), r.password());
        long refreshDays = parseDaysFromTtl(jwtProperties.getRefreshTokenTtl());
        String refreshPlain = createRefreshToken(user, refreshDays);
        long accessSecs = parseSeconds(jwtProperties.getAccessTokenTtl());
        String access = loginAccessToken(user, accessSecs * 1000L);
        return new AuthTokensResponse(access, "Bearer", accessSecs, refreshPlain, refreshDays * 24 * 3600L);
    }

    public AuthTokensResponse refresh(String refreshToken) throws Exception {
        User user = validateRefreshToken(refreshToken);
        long refreshDays = parseDaysFromTtl(jwtProperties.getRefreshTokenTtl());
        String newRefresh = rotateRefreshToken(refreshToken, refreshDays);
        long accessSecs = parseSeconds(jwtProperties.getAccessTokenTtl());
        String access = loginAccessToken(user, accessSecs * 1000L);
        return new AuthTokensResponse(access, "Bearer", accessSecs, newRefresh, refreshDays * 24 * 3600L);
    }

    public void logout(String refreshToken) throws Exception {
        revokeRefreshToken(refreshToken);
    }

    private long parseSeconds(String ttl) {
        if (ttl == null || ttl.isBlank()) return 0L;
        String s = ttl.trim().toLowerCase();
        try {
            if (s.endsWith("ms")) return Long.parseLong(s.substring(0, s.length()-2)) / 1000L;
            char last = s.charAt(s.length()-1);
            long val = Long.parseLong(s.substring(0, s.length()-1));
            switch (last) {
                case 's': return val;
                case 'm': return val * 60L;
                case 'h': return val * 3600L;
                case 'd': return val * 86400L;
                default: return Long.parseLong(s);
            }
        } catch (Exception e) { return 0L; }
    }

    private long parseDaysFromTtl(String ttl) {
        if (ttl == null || ttl.isBlank()) return 30L;
        String s = ttl.trim().toLowerCase();
        try {
            char last = s.charAt(s.length()-1);
            long val = Long.parseLong(s.substring(0, s.length()-1));
            switch (last) {
                case 'd': return val;
                case 'h': return Math.max(1L, val / 24L);
                case 'm': return Math.max(1L, val / (60L * 24L));
                case 's': return Math.max(1L, val / (24L * 3600L));
                default: return Math.max(1L, Long.parseLong(s) / (24L * 3600L));
            }
        } catch (Exception e) { return 30L; }
    }
}


