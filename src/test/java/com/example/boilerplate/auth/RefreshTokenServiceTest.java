package com.example.boilerplate.auth;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.boilerplate.auth.dto.AuthTokensResponse;
import com.example.boilerplate.user.User;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class RefreshTokenServiceTest {
  @Mock RefreshTokenRepository repo;
  @Mock JwtService jwt;

  RefreshTokenService svc;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    svc = new RefreshTokenService(repo, jwt, Duration.ofDays(30));
  }

  @Test
  void create_savesAndReturnsTokens() {
    User user = mock(User.class);
    when(user.getEmail()).thenReturn("me@example.com");

    // simulate save returns a RefreshToken via repository.save (we don't verify persisted id)
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
    when(jwt.issue(any())).thenReturn("jwt-token");
    when(jwt.expiresInSeconds()).thenReturn(900L);

    AuthTokensResponse r = svc.create(user);
    assertNotNull(r);
    assertEquals("jwt-token", r.accessToken());
    assertNotNull(r.refreshToken());
    verify(repo, atLeastOnce()).save(any());
  }

  @Test
  void revoke_deletesIfPresent() {
    String raw = "rawtoken";
    RefreshToken t = mock(RefreshToken.class);
    when(repo.findByTokenHashForUpdate(anyString())).thenReturn(Optional.of(t));
    doNothing().when(repo).delete(t);

    svc.revoke(raw);

    verify(repo).findByTokenHashForUpdate(anyString());
    verify(repo).delete(t);
  }

}
