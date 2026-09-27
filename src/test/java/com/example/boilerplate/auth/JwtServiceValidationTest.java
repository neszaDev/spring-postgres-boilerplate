package com.example.boilerplate.auth;

import com.example.boilerplate.config.JwtProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceValidationTest {

  @Test
  void constructor_whenSecretTooShort_throwsIllegalStateException() {
    JwtProperties p = new JwtProperties();
    p.setSecret("short-secret"); // less than 32 chars
    JwtService svc = new JwtService(p, null);
    assertThrows(IllegalStateException.class, svc::init);
  }
}