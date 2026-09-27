package com.example.boilerplate.auth;

import com.example.boilerplate.config.JwtProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @Test
    void generateAndValidateToken() throws Exception {
        JwtProperties props = new JwtProperties();
        props.setSecret("012345678901234567890123456789012345");
        JwtService svc = new JwtService(props, null);
        svc.init();
        String token = svc.generateToken("sub123", 1000L * 60 * 60);
        assertNotNull(token);
        assertTrue(svc.validateToken(token));
        var claims = svc.parseToken(token);
        assertEquals("sub123", claims.getBody().getSubject());
    }
}
