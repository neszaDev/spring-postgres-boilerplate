
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.boilerplate.security.JwtService;
import com.example.boilerplate.config.JwtProperties;
import com.example.boilerplate.config.JwtProperties;
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
    private PasswordEncoder encoder;
    private com.example.boilerplate.security.JwtService jwt;
    private com.example.boilerplate.config.JwtProperties jwtProperties;
    private com.example.boilerplate.service.AuthService authService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);`n        refreshTokenRepository = mock(RefreshTokenRepository.class);`n        encoder = mock(PasswordEncoder.class);`n        jwt = mock(com.example.boilerplate.security.JwtService.class);`n        jwtProperties = mock(com.example.boilerplate.config.JwtProperties.class);`n        authService = new AuthService(userRepository, refreshTokenRepository, encoder, jwt, jwtProperties);
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




