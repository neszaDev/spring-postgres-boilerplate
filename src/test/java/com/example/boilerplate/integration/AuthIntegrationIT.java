package com.example.boilerplate.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthIntegrationIT {

  @Container
  public static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("boilerplate")
          .withUsername("boilerplate")
          .withPassword("boilerplate");

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void register_login_refresh_logout_flow() throws Exception {
    Map<String, String> register =
        Map.of("email", "intuser@example.com", "password", "pw", "fullName", "Int User");
    ResponseEntity<Map> r1 = restTemplate.postForEntity("/auth/register", register, Map.class);
    assertEquals(200, r1.getStatusCodeValue());
    assertTrue(r1.getBody().containsKey("id"));

    Map<String, String> login = Map.of("email", "intuser@example.com", "password", "pw");
    ResponseEntity<Map> r2 = restTemplate.postForEntity("/auth/login", login, Map.class);
    assertEquals(200, r2.getStatusCodeValue());
    assertTrue(r2.getBody().containsKey("accessToken"));
    assertTrue(r2.getBody().containsKey("refreshToken"));
    String refresh = (String) r2.getBody().get("refreshToken");

    Map<String, String> refreq = Map.of("refreshToken", refresh);
    ResponseEntity<Map> r3 = restTemplate.postForEntity("/auth/refresh", refreq, Map.class);
    assertEquals(200, r3.getStatusCodeValue());
    assertTrue(r3.getBody().containsKey("accessToken"));
    assertTrue(r3.getBody().containsKey("refreshToken"));
    String newRefresh = (String) r3.getBody().get("refreshToken");

    Map<String, String> logout = Map.of("refreshToken", newRefresh);
    ResponseEntity<Map> r4 = restTemplate.postForEntity("/auth/logout", logout, Map.class);
    assertEquals(200, r4.getStatusCodeValue());
    assertEquals("ok", r4.getBody().get("status"));
  }
}
