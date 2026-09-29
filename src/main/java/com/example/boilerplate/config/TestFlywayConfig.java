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
          // For test profile using H2, skip statements that fail due to dialect differences.
          if (isH2 && ex.getMessage() != null && ex.getMessage().toLowerCase().contains("create extension")) {
            log.warn("Skipping script {} on H2: {}", r.getFilename(), ex.getMessage());
            continue;
          }
          throw ex;
        }
      }
    }
    return new Object();
  }
}
