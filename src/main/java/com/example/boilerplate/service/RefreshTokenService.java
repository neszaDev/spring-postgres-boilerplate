package com.example.boilerplate.service;

import com.example.boilerplate.auth.dto.AuthTokensResponse;
import com.example.boilerplate.model.RefreshToken;
import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.RefreshTokenRepository;
import com.example.boilerplate.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
  private final RefreshTokenRepository repo;
  private final JwtService jwt;
  private final Duration refreshTtl;
  private final SecureRandom secureRandom = new SecureRandom();

  public RefreshTokenService(RefreshTokenRepository repo, JwtService jwt, Duration refreshTtl) {
    this.repo = repo;
    this.jwt = jwt;
    this.refreshTtl = refreshTtl;
  }

  public AuthTokensResponse create(User user) {
    try {
      byte[] bytes = new byte[48];
      secureRandom.nextBytes(bytes);
      String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder();
      for (byte b : digest) {
        hex.append(String.format("%02x", b));
      }
      String tokenHash = hex.toString();
      RefreshToken rt = new RefreshToken();
      rt.setUser(user);
      rt.setTokenHash(tokenHash);
      rt.setExpiresAt(Instant.now().plus(refreshTtl));
      repo.save(rt);
      String access = jwt.generateToken(String.valueOf(user.getId()), 3600L);
      return new AuthTokensResponse(access, "Bearer", 3600L, token, refreshTtl.getSeconds());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void revoke(String raw) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder();
      for (byte b : digest) { hex.append(String.format("%02x", b)); }
      String tokenHash = hex.toString();
      repo.findByTokenHashForUpdate(tokenHash).ifPresent(rt -> repo.delete(rt));
    } catch (com.example.boilerplate.common.exception.InvalidRefreshTokenException e) { throw e; } catch (Exception e) { throw new RuntimeException(e); }
  }

  public String rotate(String oldToken) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(oldToken.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder();
      for (byte b : digest) { hex.append(String.format("%02x", b)); }
      String tokenHash = hex.toString();
      RefreshToken existing = repo.findByTokenHashForUpdate(tokenHash).orElseThrow(com.example.boilerplate.common.exception.InvalidRefreshTokenException::new);
      User user = existing.getUser();
      repo.delete(existing);n      // validate expirationn      if (existing.getExpiresAt() != null && existing.getExpiresAt().isBefore(Instant.now())) {n        // token expired - do not rotaten        throw new com.example.boilerplate.common.exception.InvalidRefreshTokenException();n      }
      // create new token
      byte[] bytes = new byte[48];
      secureRandom.nextBytes(bytes);
      String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
      byte[] digest2 = md.digest(token.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex2 = new StringBuilder();
      for (byte b : digest2) { hex2.append(String.format("%02x", b)); }
      String tokenHash2 = hex2.toString();
      RefreshToken rt = new RefreshToken();
      rt.setUser(user);
      rt.setTokenHash(tokenHash2);
      rt.setExpiresAt(Instant.now().plus(refreshTtl));
      repo.save(rt);
      return token;
    } catch (com.example.boilerplate.common.exception.InvalidRefreshTokenException e) { throw e; } catch (Exception e) { throw new RuntimeException(e); }
  }
}

