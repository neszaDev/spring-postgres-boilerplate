package com.example.boilerplate.config;

import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.UserRepository;
import com.example.boilerplate.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration("appConfigSecurity")
public class SecurityConfig {
  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  private final JwtService jwtService;
  private final UserRepository userRepository;

  public SecurityConfig(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/auth/**", "/actuator/health", "/actuator/info")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(
            new JwtAuthFilter(jwtService, userRepository),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  static class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
      this.jwtService = jwtService;
      this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws IOException, jakarta.servlet.ServletException {
      String header = request.getHeader("Authorization");
      if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        try {
          Jws<Claims> claims = jwtService.parseToken(token);
          String sub = claims.getBody().getSubject();
          Optional<User> ou = Optional.empty();
          try {
            Long id = Long.parseLong(sub);
            ou = userRepository.findById(id);
          } catch (NumberFormatException ex) {
          }
          if (ou.isPresent()) {
            User user = ou.get();
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
            org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(auth);
          }
        } catch (Exception ex) {
          log.debug("JWT auth failed: {}", ex.getMessage());
        }
      }
      filterChain.doFilter(request, response);
    }
  }
}

