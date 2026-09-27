package com.example.boilerplate.auth;

import com.example.boilerplate.common.exception.InvalidRefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RefreshTokenServiceRotateTest {
  @Mock RefreshTokenRepository repo;
  @Mock JwtService jwt;

  RefreshTokenService svc;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    svc = new RefreshTokenService(repo, jwt, Duration.ofDays(30));
  }

  @Test
  void rotate_whenTokenMissing_throwsInvalidRefreshTokenException() {
    when(repo.findByTokenHashForUpdate(anyString())).thenReturn(Optional.empty());
    assertThrows(InvalidRefreshTokenException.class, () -> svc.rotate("raw"));
    verify(repo).findByTokenHashForUpdate(anyString());
  }

  @Test
  void rotate_whenTokenExpired_throwsInvalidRefreshTokenException() {
    RefreshToken token = mock(RefreshToken.class);
    when(token.getExpiresAt()).thenReturn(Instant.now().minusSeconds(60));
    when(repo.findByTokenHashForUpdate(anyString())).thenReturn(Optional.of(token));
    assertThrows(InvalidRefreshTokenException.class, () -> svc.rotate("raw"));
    verify(repo).delete(token);
  }
}
