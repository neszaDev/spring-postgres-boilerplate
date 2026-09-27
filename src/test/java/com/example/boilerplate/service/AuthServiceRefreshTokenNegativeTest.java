package com.example.boilerplate.service;

import com.example.boilerplate.model.RefreshToken;
import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.RefreshTokenRepository;
import com.example.boilerplate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceRefreshTokenNegativeTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        authService = new AuthService(userRepository, refreshTokenRepository, null, null);
    }

    @Test
    void validateRefreshToken_expired_shouldThrow() {
        RefreshToken rt = new RefreshToken();
        rt.setExpiresAt(Instant.now().minusSeconds(3600));
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(rt));

        assertThrows(IllegalArgumentException.class, () -> authService.validateRefreshToken("any-token"));
    }

    @Test
    void rotateRefreshToken_invalid_shouldThrow() {
        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authService.rotateRefreshToken("bad", 30));
    }

}
