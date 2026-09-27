package com.example.boilerplate.auth;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.boilerplate.model.RefreshToken;
import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.RefreshTokenRepository;
import com.example.boilerplate.repository.UserRepository;
import com.example.boilerplate.security.JwtService;
import com.example.boilerplate.config.JwtProperties;
import com.example.boilerplate.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

class AuthServiceRefreshTokenLimitTest {
  @Mock RefreshTokenRepository rtRepo;
  @Mock UserRepository userRepo;
  @Mock org.springframework.security.crypto.password.PasswordEncoder pwd;
  @Mock JwtService jwt;

  AuthService svc;
  JwtProperties props;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    props = new JwtProperties();
    props.setMaxRefreshTokens(2);
    svc = new AuthService(userRepo, rtRepo, pwd, jwt, props);
  }

  @Test
  void createRefreshToken_prunesOldTokens() throws Exception {
    User u = new User(); u.setId(1L);
    RefreshToken old1 = mock(RefreshToken.class);
    RefreshToken old2 = mock(RefreshToken.class);
    when(rtRepo.findByUserOrderByExpiresAtAsc(u)).thenReturn(Arrays.asList(old1, old2));

    String token = svc.createRefreshToken(u, 30);
    assertNotNull(token);
    // expect repo.delete called at least once to prune oldest
    verify(rtRepo, atLeastOnce()).delete(any(RefreshToken.class));
    verify(rtRepo).save(any(RefreshToken.class));
  }
}