package com.example.boilerplate.security;

import com.example.boilerplate.security.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;

  public JwtAuthenticationFilter(JwtService jwt) {
    this.jwt = jwt;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer "))
      try {
        var jws = jwt.parseToken(h.substring(7));
        var claims = jws.getBody();
        var auth =
            new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                java.util.List.of(
                    new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class))));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (JwtException ignored) {
      }
    chain.doFilter(req, res);
  }
}


