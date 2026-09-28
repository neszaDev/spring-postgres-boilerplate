package com.example.boilerplate.security;

import com.example.boilerplate.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private static final Logger log = LoggerFactory.getLogger(JwtService.class);
  private final JwtProperties properties;
  private final Environment env;
  private Key hmacKey;

  @Autowired
  public JwtService(JwtProperties properties, Environment env) {
    this.properties = properties;
    this.env = env;
  }

  @PostConstruct
  public void init() {
    String secret = properties.getSecret();
    boolean isTest = false;
    if (this.env != null) {
      for (String p : this.env.getActiveProfiles()) {
        if ("test".equalsIgnoreCase(p)) {
          isTest = true;
          break;
        }
      }
    }

    if ((secret == null || secret.isBlank())) {
      if (!isTest) {
        log.error(
            "JWT secret is not set. Set environment variable JWT_SECRET or configure"
                + " app.security.jwt.secret");
        throw new IllegalStateException("JWT secret not configured");
      } else {
        log.warn("JWT secret not set but running with 'test' profile; skipping validation");
      }
    }

    if (!isTest
        && ("00000000000000000000000000000000".equals(secret)
            || (secret != null && secret.length() < 32))) {
      log.error(
          "JWT secret is insecure or too short. Provide a secure random 32+ character secret in"
              + " app.security.jwt.secret");
      throw new IllegalStateException(
          "JWT secret missing or invalid (too short or default placeholder)");
    }

    if (secret != null) {
      this.hmacKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      log.info("JWT service initialized with key length {}.", secret.length());
    } else {
      // For test profile create a fallback weak key to avoid NPEs in tests
      this.hmacKey =
          Keys.hmacShaKeyFor("00000000000000000000000000000000".getBytes(StandardCharsets.UTF_8));
      log.warn("JWT service initialized with fallback test key.");
    }
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
