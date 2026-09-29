package com.example.boilerplate.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@Configuration
@Profile("test")
public class TestFlywayConfig {
  private static final Logger log = LoggerFactory.getLogger(TestFlywayConfig.class);

  @Bean
  public Object sqlMigrateRunner(DataSource dataSource) throws Exception {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources("classpath:db/migration/*");
    // Sort resources by filename to ensure ordering
    Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
    try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
      String dbName = conn.getMetaData().getDatabaseProductName();
      boolean isH2 = dbName != null && dbName.toLowerCase().contains("h2");
      for (Resource r : resources) {
        if (!r.exists()) continue;
        if (isH2) {
          // Pre-filter Postgres-only constructs for H2 and apply the filtered script
            try {
              String content = new String(r.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
              // Filter line-by-line to avoid BOM/case/whitespace edge-cases that break regex anchors.
              StringBuilder sb = new StringBuilder();
              for (String line : content.split("\\R")) {
                String trimmed = line;
                if (trimmed.length() > 0 && trimmed.charAt(0) == '\uFEFF') {
                  trimmed = trimmed.substring(1);
                }
                String t = trimmed.trim().toLowerCase();
                if (t.startsWith("create extension")) {
                  // Skip Postgres-only extension statements on H2
                  continue;
                }
                sb.append(trimmed).append("\n");
              }
              String filtered = sb.toString();
              // H2 compatibility substitutions
              filtered = filtered.replaceAll("(?i)BIGSERIAL", "BIGINT AUTO_INCREMENT");
              filtered = filtered.replaceAll("(?i)SERIAL", "INTEGER AUTO_INCREMENT");
              filtered = filtered.replaceAll("(?i)TIMESTAMP WITH TIME ZONE", "TIMESTAMP");
              filtered = filtered.replaceAll("(?i)timestamptz", "TIMESTAMP");
              java.io.File tmp = java.io.File.createTempFile("flyway-", ".sql");
              java.nio.file.Files.writeString(tmp.toPath(), filtered, java.nio.charset.StandardCharsets.UTF_8);
              tmp.deleteOnExit();
              org.springframework.core.io.FileSystemResource fr = new org.springframework.core.io.FileSystemResource(tmp);
              ScriptUtils.executeSqlScript(conn, fr);
            } catch (Exception ex2) {
              log.error("Filtered script execution failed for {}: {}", r.getFilename(), ex2.getMessage());
              throw ex2 instanceof RuntimeException ? (RuntimeException) ex2 : new RuntimeException(ex2);
            }
        } else {
          // Execute SQL script; ScriptUtils will handle encoding and separators
          ScriptUtils.executeSqlScript(conn, r);
        }
      }
    }
    return new Object();
  }
}
