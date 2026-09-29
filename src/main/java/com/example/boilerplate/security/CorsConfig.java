package com.example.boilerplate.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.*;

@Configuration
public class CorsConfig {
  @Bean
  CorsConfigurationSource corsConfigurationSource(Environment env) {
    String allowed = env.getProperty("app.cors.allowed-origins");
    List<String> origins;
    if (allowed == null || allowed.isBlank()) {
      origins = List.of("*");
    } else {
      // support YAML/list or comma-separated values
      String cleaned = allowed.trim();
      if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
        cleaned = cleaned.substring(1, cleaned.length() - 1);
      }
      origins = Arrays.stream(cleaned.split(","))
          .map(s -> s.replace("\"", "").trim())
          .filter(s -> !s.isEmpty())
          .collect(Collectors.toList());
      if (origins.isEmpty()) {
        origins = List.of("*");
      }
    }

    var c = new CorsConfiguration();
    c.setAllowedOrigins(origins);
    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    c.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-Id"));
    var s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }
}
