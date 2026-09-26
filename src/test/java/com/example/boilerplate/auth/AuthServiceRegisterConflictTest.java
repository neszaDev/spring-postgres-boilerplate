package com.example.boilerplate.auth;

import com.example.boilerplate.common.exception.ConflictException;
import com.example.boilerplate.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;`nimport static org.mockito.Mockito.verify;

class AuthServiceRegisterConflictTest {
  @Mock UserRepository users;
  @Mock PasswordEncoder encoder;
  @Mock RefreshTokenService refreshTokens;
  AuthService service;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    service = new AuthService(users, encoder, refreshTokens);
  }

  @Test
  void register_whenEmailExists_throwsConflictException() {
    when(users.existsByEmail(anyString())).thenReturn(true);
    var req = new com.example.boilerplate.auth.dto.RegisterRequest("me@example.com", "pw");
    assertThrows(ConflictException.class, () -> service.register(req));
    verify(users).existsByEmail(anyString());
  }
}

