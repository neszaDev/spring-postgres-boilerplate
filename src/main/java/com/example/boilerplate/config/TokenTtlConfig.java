package com.example.boilerplate.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenTtlConfig {

  @Bean
  public Duration refreshTtl(JwtProperties jwtProperties) {
    return parseDuration(jwtProperties.getRefreshTokenTtl());
  }

  private Duration parseDuration(String s) {
    if (s == null || s.isBlank()) {
      return Duration.ofDays(30);
    }
    s = s.trim().toLowerCase();
    try {
      if (s.endsWith("d")) {
        long v = Long.parseLong(s.substring(0, s.length()-1));
        return Duration.ofDays(v);
      } else if (s.endsWith("h")) {
        long v = Long.parseLong(s.substring(0, s.length()-1));
        return Duration.ofHours(v);
      } else if (s.endsWith("m")) {
        long v = Long.parseLong(s.substring(0, s.length()-1));
        return Duration.ofMinutes(v);
      } else if (s.endsWith("s")) {
        long v = Long.parseLong(s.substring(0, s.length()-1));
        return Duration.ofSeconds(v);
      } else {
        // treat as seconds
        long v = Long.parseLong(s);
        return Duration.ofSeconds(v);
      }
    } catch (Exception e) {
      return Duration.ofDays(30);
    }
  }
}