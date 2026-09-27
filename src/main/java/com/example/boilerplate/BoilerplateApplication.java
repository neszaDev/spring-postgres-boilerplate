package com.example.boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;\nimport org.springframework.boot.context.properties.EnableConfigurationProperties;\nimport com.example.boilerplate.config.JwtProperties;

@SpringBootApplication\n@EnableConfigurationProperties(JwtProperties.class)
public class BoilerplateApplication {
  public static void main(String[] args) {
    SpringApplication.run(BoilerplateApplication.class, args);
  }
}

