package com.example.boilerplate.auth;

import com.example.boilerplate.repository.RefreshTokenRepository;
import com.example.boilerplate.service.RefreshTokenCleanupService;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RefreshTokenCleanupServiceTest {
  @Mock com.example.boilerplate.repository.RefreshTokenRepository repo;
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







