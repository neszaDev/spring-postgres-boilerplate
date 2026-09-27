package com.example.boilerplate.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.example.boilerplate.config.JwtProperties;
import com.example.boilerplate.security.JwtService;
import org.junit.jupiter.api.Test;

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
