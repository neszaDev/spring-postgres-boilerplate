package com.example.boilerplate;

import com.example.boilerplate.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableScheduling
public class BoilerplateApplication {
  public static void main(String[] args) {
    SpringApplication.run(BoilerplateApplication.class, args);
  }
}
