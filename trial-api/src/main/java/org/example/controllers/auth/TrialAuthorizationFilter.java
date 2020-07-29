package org.example.controllers.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.example.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.GenericFilterBean;

@Log4j2
public class TrialAuthorizationFilter extends GenericFilterBean {

  private final String secret;
  private final UserService userService;

  public TrialAuthorizationFilter(String secret, UserService userService) {
    this.secret = secret;
    this.userService = userService;
  }

  private Authentication parseToken(HttpServletRequest request) {
    String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (auth != null && auth.startsWith("Bearer ")) {
      String claims = auth.substring(7);
      try {
        Jws<Claims> claimsJws =
            Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(claims);

        String username = claimsJws.getBody().getSubject();

        if ("".equals(username) || username == null) {
          return null;
        }

        List<String> roles = userService.getRolesForUser(username);

        List<GrantedAuthority> authorities =
            roles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toList());

        User u = new User(username, "", authorities);

        return new UsernamePasswordAuthenticationToken(u, null, authorities);
      } catch (JwtException exception) {
        log.warn("Authorization failed {} for token: {}", exception.getMessage(), auth);
      }
    }

    return null;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    Authentication authentication = parseToken((HttpServletRequest) request);

    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } else {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
