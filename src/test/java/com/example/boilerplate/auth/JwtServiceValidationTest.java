package com.example.boilerplate.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.boilerplate.config.JwtProperties;
import com.example.boilerplate.security.JwtService;
import org.junit.jupiter.api.Test;

class JwtServiceValidationTest {

  @Test
  void constructor_whenSecretTooShort_throwsIllegalStateException() {
    JwtProperties p = new JwtProperties();
    p.setSecret("short-secret"); // less than 32 chars
    JwtService svc = new JwtService(p, null);
    assertThrows(IllegalStateException.class, svc::init);
  }
}
