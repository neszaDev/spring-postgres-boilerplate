package com.example.boilerplate.service;

import com.example.boilerplate.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

public class RefreshTokenCleanupServiceTest {

    @Test
    void cleanup_calls_repository() {
        RefreshTokenRepository repo = Mockito.mock(RefreshTokenRepository.class);
        RefreshTokenCleanupService svc = new RefreshTokenCleanupService(repo);
        svc.cleanupExpiredRefreshTokens();
        Mockito.verify(repo).deleteByExpiresAtBefore(Mockito.any(Instant.class));
    }
}
