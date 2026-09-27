package com.example.boilerplate.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.boilerplate.auth.dto.RegisterRequest;
import com.example.boilerplate.common.exception.ConflictException;
import com.example.boilerplate.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {
  @Mock com.example.boilerplate.repository.UserRepository users;\n  @Mock PasswordEncoder encoder;\n  @Mock com.example.boilerplate.repository.RefreshTokenRepository refreshTokenRepository;\n  @Mock com.example.boilerplate.security.JwtService jwtService;\n  @Mock com.example.boilerplate.config.JwtProperties jwtProperties;

  @Test
  void rejectsDuplicateEmail() {
    when(users.existsByEmail("a@example.com")).thenReturn(true);
    var service = new AuthService(users, refreshTokenRepository, encoder, jwtService, jwtProperties);
    assertThrows(
        ConflictException.class,
        () -> service.register(new RegisterRequest("a@example.com", "a-long-enough-password")));
    verifyNoMoreInteractions(encoder);
  }
}



