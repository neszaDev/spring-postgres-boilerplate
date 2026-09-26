package com.example.boilerplate.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.boilerplate.auth.dto.AuthTokensResponse;
import com.example.boilerplate.auth.dto.RefreshTokenRequest;
import com.example.boilerplate.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationIT {
  @Container
  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

  @DynamicPropertySource
  static void database(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired TestRestTemplate rest;
  @LocalServerPort int port;

  private String url(String path) {
    return "http://localhost:" + port + path;
  }

  @Test
  void register_login_refresh_and_logout_flow() {
    var register = new RegisterRequest("e2e-user@example.com", "password");
    ResponseEntity<AuthTokensResponse> regResp =
        rest.postForEntity(url("/api/v1/auth/register"), register, AuthTokensResponse.class);
    assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    AuthTokensResponse tokens = regResp.getBody();
    assertThat(tokens).isNotNull();
    assertThat(tokens.refreshToken()).isNotBlank();

    var refreshReq = new RefreshTokenRequest(tokens.refreshToken());
    ResponseEntity<AuthTokensResponse> rot =
        rest.postForEntity(url("/api/v1/auth/refresh"), refreshReq, AuthTokensResponse.class);
    assertThat(rot.getStatusCode()).isEqualTo(HttpStatus.OK);
    AuthTokensResponse newTokens = rot.getBody();
    assertThat(newTokens).isNotNull();
    assertThat(newTokens.accessToken()).isNotEqualTo(tokens.accessToken());

    var logoutReq = new RefreshTokenRequest(newTokens.refreshToken());
    ResponseEntity<Void> logoutResp =
        rest.postForEntity(url("/api/v1/auth/logout"), logoutReq, Void.class);
    assertThat(logoutResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<String> afterLogout =
        rest.postForEntity(
            url("/api/v1/auth/refresh"),
            new RefreshTokenRequest(newTokens.refreshToken()),
            String.class);
    assertThat(afterLogout.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }
}
