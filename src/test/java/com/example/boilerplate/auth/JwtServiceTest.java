package com.example.boilerplate.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.example.boilerplate.user.User;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  @Test
  void issueAndParseToken() {
    String secret = "01234567890123456789012345678901"; // 32 bytes
    JwtService svc = new JwtService(secret, Duration.ofMinutes(15));
    User u = new User("me@example.com", "pw");
    String token = svc.issue(u);
    assertNotNull(token);
    Claims claims = svc.parse(token);
    assertEquals("me@example.com", claims.getSubject());
    assertEquals("USER", claims.get("role", String.class));
  }
}
