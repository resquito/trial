package org.example.controllers.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Log4j2
public class TrialAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final String secret;
  private final String issuer;
  private final String audience;
  private final String type;

  public TrialAuthenticationFilter(
      final AuthenticationManager authenticationManager,
      final String audience,
      final String issuer,
      final String secret,
      final String type) {
    this.audience = audience;
    this.issuer = issuer;
    this.secret = secret;
    this.type = type;
    this.setAuthenticationManager(authenticationManager);

    setFilterProcessesUrl("/login");
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain,
      Authentication authentication) {
    User user = (User) authentication.getPrincipal();

    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    String token =
        Jwts.builder()
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .setHeaderParam("typ", type)
            .setIssuer(issuer)
            .setAudience(audience)
            .setSubject(user.getUsername())
            .setExpiration(new Date(ZonedDateTime.now().toInstant().toEpochMilli() + 6000000L))
            .compact();

    response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
  }
}
