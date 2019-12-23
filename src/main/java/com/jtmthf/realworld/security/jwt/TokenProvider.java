package com.jtmthf.realworld.security.jwt;

import com.jtmthf.realworld.config.ApplicationProperties;
import com.jtmthf.realworld.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.Collections;

@Component
public class TokenProvider implements InitializingBean {
  private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

  private Key key;

  private final ApplicationProperties applicationProperties;

  public TokenProvider(ApplicationProperties properties) {
    this.applicationProperties = properties;
  }

  @Override
  public void afterPropertiesSet() {
    byte[] keyBytes;
    String secret = applicationProperties.getSecurity().getAuthentication().getJwt().getSecret();
    if (!StringUtils.isEmpty(secret)) {
      log.warn(
        "Warning: the JWT key used is not Base64-encoded. " +
          "It's recommended using the `application.security.authentication.jwt.base64-secret` key for optimum security."
      );
      keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    } else {
      log.debug("Using a Base64-encoded JWT secret key");
      keyBytes =
        Decoders.BASE64.decode(
          applicationProperties.getSecurity().getAuthentication().getJwt().getBase64Secret()
        );
    }
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  public String createToken(Authentication authentication) {
    if (!(authentication.getPrincipal() instanceof User)) {
      throw new IllegalStateException("principal must be a User");
    }

    return Jwts.builder()
      .setSubject(authentication.getName())
      .claim("id", ((User) authentication.getPrincipal()).getId())
      .signWith(key, SignatureAlgorithm.HS512)
      .setExpiration(
        Date.from(
          Instant.now()
            .plus(
              applicationProperties.getSecurity().getAuthentication().getJwt().getTokenValidity()
            )
        )
      )
      .compact();
  }

  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

    User principal = User.fromClaims(claims.getSubject(), claims.get("id", Long.class));

    return new UsernamePasswordAuthenticationToken(principal, token, Collections.emptyList());
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("Invalid JWT signature.");
      log.trace("Invalid JWT signature trace:", e);
    } catch (ExpiredJwtException e) {
      log.info("Expired JWT token.");
      log.trace("Expired JWT token trace:", e);
    } catch (UnsupportedJwtException e) {
      log.info("Unsupported JWT token.");
      log.trace("Unsupported JWT token trace:", e);
    } catch (IllegalArgumentException e) {
      log.info("JWT token compact of handler are invalid.");
      log.trace("JWT token compact of handler are invalid trace:", e);
    }
    return false;
  }
}
