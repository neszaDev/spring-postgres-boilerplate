package com.example.boilerplate.auth;

import com.example.boilerplate.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final JwtProperties properties;
    private Key hmacKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        String secret = properties.getSecret();
        if (secret == null || secret.isBlank()) {
            log.error("JWT secret is not set. Set environment variable JWT_SECRET or configure app.security.jwt.secret");
            throw new IllegalStateException("JWT secret not configured");
        }
        if ("00000000000000000000000000000000".equals(secret) || secret.length() < 32) {
            log.error("JWT secret is insecure or too short. Provide a secure random 32+ character secret in app.security.jwt.secret");
            throw new IllegalStateException("JWT secret missing or invalid (too short or default placeholder)");
        }

        this.hmacKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT service initialized with secure key (length={}).", secret.length());
    }

    public String generateToken(String subject, long ttlMillis) {
        long now = System.currentTimeMillis();
        Date exp = new Date(now + ttlMillis);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(exp)
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(token);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
