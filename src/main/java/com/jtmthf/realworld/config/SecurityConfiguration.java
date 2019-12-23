package com.jtmthf.realworld.config;

import com.jtmthf.realworld.security.jwt.JWTConfigurer;
import com.jtmthf.realworld.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  private final TokenProvider tokenProvider;

  public SecurityConfiguration(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.csrf().disable().exceptionHandling().authenticationEntryPoint(
        null
      ).accessDeniedHandler(null).and().headers().frameOptions().disable().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().antMatchers("/api/authenticate").permitAll().antMatchers("/api/register").permitAll().antMatchers("/api/activate").permitAll().antMatchers("/api/account/reset-password/init").permitAll().antMatchers("/api/account/reset-password/finish").permitAll().antMatchers("/api/**").authenticated().antMatchers("/management/health").permitAll().antMatchers("/management/info").permitAll().antMatchers("/management/**").authenticated().and().apply(securityConfigurerAdapter());
  // @formatter:on
  }

  private JWTConfigurer securityConfigurerAdapter() {
    return new JWTConfigurer(tokenProvider);
  }
}
