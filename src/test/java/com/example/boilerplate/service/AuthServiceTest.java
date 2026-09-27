package com.example.boilerplate.service;

import com.example.boilerplate.config.JwtProperties;
import com.example.boilerplate.security.JwtService;
import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.RefreshTokenRepository;
import com.example.boilerplate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authService = new AuthService(userRepository, refreshTokenRepository, passwordEncoder, jwtService, jwtProperties);
    }

    @Test
    void register_creates_user_when_not_exists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        User u = authService.register("a@b.com","pwd","Full");
        assertNotNull(u);
        assertEquals("a@b.com", u.getEmail());
        assertEquals("hashed", u.getPasswordHash());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void authenticate_success_and_failure() {
        User u = new User();
        u.setEmail("a@b.com");
        u.setPasswordHash("hashed");
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pwd","hashed")).thenReturn(true);

        User auth = authService.authenticate("a@b.com","pwd");
        assertEquals("a@b.com", auth.getEmail());

        when(passwordEncoder.matches("bad","hashed")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate("a@b.com","bad"));
    }

    @Test
    void createRefreshToken_calls_repository_save() throws Exception {
        User u = new User(); u.setId(1L);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        String token = authService.createRefreshToken(u, 30);
        assertNotNull(token);
        verify(refreshTokenRepository, times(1)).save(any());
    }
}





