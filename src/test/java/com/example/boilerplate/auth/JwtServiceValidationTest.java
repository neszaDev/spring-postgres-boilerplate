package com.example.boilerplate.auth;

import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceValidationTest {

  @Test
  void constructor_whenSecretTooShort_throwsIllegalStateException() {
    String shortSecret = "short-secret"; // less than 32 bytes
    assertThrows(IllegalStateException.class, () -> new JwtService(shortSecret, Duration.ofMinutes(15)));
  }
}
