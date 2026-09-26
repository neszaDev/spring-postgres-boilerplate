package com.example.boilerplate.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtServiceValidationTest {

  @Test
  void constructor_whenSecretTooShort_throwsIllegalStateException() {
    String shortSecret = "short-secret"; // less than 32 bytes
    assertThrows(
        IllegalStateException.class, () -> new JwtService(shortSecret, Duration.ofMinutes(15)));
  }
}
