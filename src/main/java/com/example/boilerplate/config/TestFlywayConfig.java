package com.example.boilerplate.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Comparator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@Configuration
@Profile("test")
public class TestFlywayConfig {

  @Bean
  public Object sqlMigrateRunner(DataSource dataSource) throws Exception {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources("classpath:db/migration/*");
    // Sort resources by filename to ensure ordering
    Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
    try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
      for (Resource r : resources) {
        if (!r.exists()) continue;
        // Execute SQL script; ScriptUtils will handle encoding and separators
        ScriptUtils.executeSqlScript(conn, r);
      }
    }
    return new Object();
  }
}
