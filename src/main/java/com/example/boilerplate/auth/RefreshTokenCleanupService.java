package com.example.boilerplate.auth;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCleanupService {
  private final RefreshTokenRepository tokens;

  public RefreshTokenCleanupService(RefreshTokenRepository tokens) {
    this.tokens = tokens;
  }

  // runs every 24h by default; override with app.security.refresh-token-cleanup-ms
  @Scheduled(fixedDelayString = "${app.security.refresh-token-cleanup-ms:86400000}")
  public void cleanupExpired() {
    tokens.deleteByExpiresAtBefore(Instant.now());
  }
}
