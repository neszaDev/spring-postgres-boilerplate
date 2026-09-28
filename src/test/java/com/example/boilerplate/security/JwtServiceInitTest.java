package com.example.boilerplate.security;

import com.example.boilerplate.config.JwtProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceInitTest {

  @Test
  void initShouldFailWhenSecretMissingAndNotTestProfile() {
    JwtProperties props = new JwtProperties();
    JwtService svc = new JwtService(props, $null);
    assertThrows(IllegalStateException.class, svc::init);
  }
}
