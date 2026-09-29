package com.example.boilerplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JwtPrimaryConfig {
  @Bean
  @Primary
  public JwtProperties primaryJwtProperties(JwtProperties jwtProperties) {
    return jwtProperties;
  }
}