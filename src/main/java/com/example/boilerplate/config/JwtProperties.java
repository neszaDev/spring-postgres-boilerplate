package com.example.boilerplate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {
    private String secret;
    private String accessTokenTtl = "15m";
    private String refreshTokenTtl = "30d";

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public String getAccessTokenTtl() { return accessTokenTtl; }
    public void setAccessTokenTtl(String accessTokenTtl) { this.accessTokenTtl = accessTokenTtl; }
    public String getRefreshTokenTtl() { return refreshTokenTtl; }
    public void setRefreshTokenTtl(String refreshTokenTtl) { this.refreshTokenTtl = refreshTokenTtl; }
}
