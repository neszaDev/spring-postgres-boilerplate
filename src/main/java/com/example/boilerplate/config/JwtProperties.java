package com.example.boilerplate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {
  private String secret;
  private String accessTokenTtl = "15m";
  private String refreshTokenTtl = "30d";

  \n  private int maxRefreshTokens = 5;\npublic String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getAccessTokenTtl() {
    return accessTokenTtl;
  }

  public void setAccessTokenTtl(String accessTokenTtl) {
    this.accessTokenTtl = accessTokenTtl;
  }

  \n\n  public int getMaxRefreshTokens() {\n    return maxRefreshTokens;\n  }\n\n  public void setMaxRefreshTokens(int maxRefreshTokens) {\n    this.maxRefreshTokens = maxRefreshTokens;\n  }

  public void setRefreshTokenTtl(String refreshTokenTtl) {
    this.refreshTokenTtl = refreshTokenTtl;
  }
}

