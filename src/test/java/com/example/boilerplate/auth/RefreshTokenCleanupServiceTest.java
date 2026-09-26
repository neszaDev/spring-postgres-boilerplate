package com.example.boilerplate.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RefreshTokenCleanupServiceTest {
  @Mock RefreshTokenRepository repo;
  RefreshTokenCleanupService svc;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    svc = new RefreshTokenCleanupService(repo);
  }

  @Test
  void cleanup_callsDelete() {
    svc.cleanupExpired();
    verify(repo, times(1)).deleteByExpiresAtBefore(org.mockito.ArgumentMatchers.any(Instant.class));
  }
}
