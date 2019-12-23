package com.jtmthf.realworld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.security.Security;
import java.time.Duration;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
  private Security security = new Security();

  public Security getSecurity() {
    return security;
  }

  public static class Security {
    private Authentication authentication = new Authentication();

    public Authentication getAuthentication() {
      return authentication;
    }

    public static class Authentication {
      private Jwt jwt = new Jwt();

      public Jwt getJwt() {
        return jwt;
      }

      public static class Jwt {
        private String secret;
        private String base64Secret;
        private Duration tokenValidity = Duration.ofMinutes(10);

        public String getSecret() {
          return secret;
        }

        public void setSecret(String secret) {
          this.secret = secret;
        }

        public String getBase64Secret() {
          return base64Secret;
        }

        public void setBase64Secret(String base64Secret) {
          this.base64Secret = base64Secret;
        }

        public Duration getTokenValidity() {
          return tokenValidity;
        }

        public void setTokenValidity(Duration tokenValidity) {
          this.tokenValidity = tokenValidity;
        }
      }
    }
  }
}
