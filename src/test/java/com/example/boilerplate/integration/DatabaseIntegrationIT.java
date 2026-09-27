package com.example.boilerplate.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseIntegrationIT {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("boilerplate")
            .withUsername("boilerplate")
            .withPassword("boilerplate");

    @Autowired
    private DataSource dataSource;

    @Test
    public void flywayMigrationsApply() throws Exception {
        // Run Flyway programmatically against the Testcontainers database
        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();

        // assert that Flyway applied at least one migration
        assertTrue(flyway.info().applied().length > 0, "Expected at least one applied migration");

        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            // simple query to validate that users table exists after migration
            ResultSet rs = st.executeQuery("SELECT to_regclass('public.users') as tbl");
            if (rs.next()) {
                String tbl = rs.getString("tbl");
                assertTrue(tbl != null && (tbl.equals("users") || tbl.equals("public.users") || tbl.contains("users")));
            } else {
                throw new IllegalStateException("no result from regclass query");
            }
        }
    }
}

