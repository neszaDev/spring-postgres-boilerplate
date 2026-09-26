package com.example.boilerplate.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DatabaseIntegrationIT {

  @Container
  public static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("boilerplate")
          .withUsername("boilerplate")
          .withPassword("boilerplate");

  @Test
  void flyway_migrations_apply_and_table_exists() throws Exception {
    String jdbcUrl = postgres.getJdbcUrl();
    String username = postgres.getUsername();
    String password = postgres.getPassword();

    DriverManagerDataSource ds = new DriverManagerDataSource(jdbcUrl, username, password);
    ds.setDriverClassName("org.postgresql.Driver");
    JdbcTemplate jdbc = new JdbcTemplate(ds);

    // simple existence check for users table created by Flyway migrations
    Integer count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'",
            Integer.class);
    assertTrue(count != null && count > 0, "users table should exist after migrations");
  }
}
