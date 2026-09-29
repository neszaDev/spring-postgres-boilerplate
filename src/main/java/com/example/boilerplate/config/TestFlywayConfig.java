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
        try {
          // Execute SQL script; ScriptUtils will handle encoding and separators
          ScriptUtils.executeSqlScript(conn, r);
        } catch (ScriptStatementFailedException ex) {
          // H2 does not support certain Postgres-specific statements (e.g., CREATE EXTENSION pgcrypto).
          // If running against H2 and the failure looks like a CREATE EXTENSION issue, try a fallback:
          // remove CREATE EXTENSION lines and re-run the script so the rest of the SQL can apply.
          if (isH2 && ex.getMessage() != null && ex.getMessage().toLowerCase().contains("create extension")) {
            try {
              log.warn("Detected Postgres-only statement in {}: {}; attempting to apply remainder of script on H2", r.getFilename(), ex.getMessage());
              String content = new String(r.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
              // Remove CREATE EXTENSION lines
              String filtered = content.replaceAll("(?i)^\\s*CREATE\\s+EXTENSION.*$\\n?", "");
              // H2 compatibility substitutions
              filtered = filtered.replaceAll("(?i)BIGSERIAL", "BIGINT AUTO_INCREMENT");
              filtered = filtered.replaceAll("(?i)SERIAL", "INTEGER AUTO_INCREMENT");
              filtered = filtered.replaceAll("(?i)TIMESTAMP WITH TIME ZONE", "TIMESTAMP");
              filtered = filtered.replaceAll("(?i)timestamptz", "TIMESTAMP");
              // Write filtered content to a temp file and execute
              java.io.File tmp = java.io.File.createTempFile("flyway-", ".sql");
              java.nio.file.Files.writeString(tmp.toPath(), filtered, java.nio.charset.StandardCharsets.UTF_8);
              tmp.deleteOnExit();
              org.springframework.core.io.FileSystemResource fr = new org.springframework.core.io.FileSystemResource(tmp);
              ScriptUtils.executeSqlScript(conn, fr);
            } catch (Exception ex2) {
              log.error("Fallback execution of filtered script {} failed: {}", r.getFilename(), ex2.getMessage());
              throw ex2 instanceof RuntimeException ? (RuntimeException) ex2 : new RuntimeException(ex2);
            }
            continue;
          }
          throw ex;
        }
      }
    }
    return new Object();
  }
}
