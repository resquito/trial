package org.example.controllers.auth;

import javax.sql.DataSource;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final String secret;
  private final String issuer;
  private final String audience;
  private final String type;

  private final UserService userService;

  @Autowired
  public WebSecurityConfig(
      @Value("${trial.jwt.audience}") String audience,
      @Value("${trial.jwt.issuer}") String issuer,
      @Value("${trial.jwt.secret}") String secret,
      @Value("${trial.jwt.type}") String type,
      final UserService userService) {
    this.secret = secret;
    this.issuer = issuer;
    this.audience = audience;
    this.type = type;
    this.userService = userService;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers(
            "/swagger-ui.html", "/swagger-ui/*", "/v3/api-docs/swagger-config", "/v3/api-docs");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("**")
        .authenticated()
        .antMatchers(
            "/swagger-ui.html", "/swagger-ui/*", "/v3/api-docs/swagger-config", "/v3/api-docs")
        .permitAll()
        .and()
        .addFilterAfter(
            new TrialAuthorizationFilter(secret, userService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterAt(
            new TrialAuthenticationFilter(
                authenticationManagerBean(), audience, issuer, secret, type),
            UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Autowired
  public void configureGlobal(DataSource dataSource, AuthenticationManagerBuilder auth)
      throws Exception {
    auth.jdbcAuthentication()
        .dataSource(dataSource)
        .passwordEncoder(new BCryptPasswordEncoder())
        .usersByUsernameQuery("select username,password,enabled from user where username = ?")
        .authoritiesByUsernameQuery(
            "select username,role"
                + " from role r, user u, user_roles ur "
                + " where u.username = ? "
                + " and u.user_id = ur.user_id"
                + " and r.role_id = ur.role_id");
  }
}
