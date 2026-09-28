package com.example.boilerplate.service;

import com.example.boilerplate.repository.RefreshTokenRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenCleanupService {
  private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupService.class);
  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
  }

  // Run once a day
  @Scheduled(cron = "0 0 3 * * ?")
  @Transactional
  public void cleanupExpiredRefreshTokens() {
    Instant now = Instant.now();
    log.info("Running refresh token cleanup at {}", now);
    refreshTokenRepository.deleteByExpiresAtBefore(now);
  }
}
