package com.example.boilerplate.security;

import com.example.boilerplate.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {
  JwtService jwt = Mockito.mock(JwtService.class);
  JwtAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new JwtAuthenticationFilter(jwt);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_setsAuthentication_whenTokenValid() throws Exception {
    Claims claims = Jwts.claims();
    claims.setSubject("me@example.com");
    claims.put("role", "USER");

    when(jwt.parse(anyString())).thenReturn(claims);

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.addHeader("Authorization", "Bearer token");
    MockHttpServletResponse res = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilterInternal(req, res, chain);

    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertEquals("me@example.com", auth.getName());
    assertEquals(1, auth.getAuthorities().size());
  }
}
