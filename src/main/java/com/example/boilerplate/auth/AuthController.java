package com.example.boilerplate.auth;

import com.example.boilerplate.auth.dto.*;
import com.example.boilerplate.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthTokensResponse register(@Valid @RequestBody RegisterRequest r) throws Exception {
    return service.register(r);
  }

  @PostMapping("/login")
  public AuthTokensResponse login(@Valid @RequestBody LoginRequest r) throws Exception {
    return service.login(r);
  }

  @PostMapping("/refresh")
  public AuthTokensResponse refresh(@Valid @RequestBody RefreshTokenRequest r) throws Exception {
    return service.refresh(r.refreshToken());
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@Valid @RequestBody RefreshTokenRequest r) throws Exception {
    service.logout(r.refreshToken());
  }
}

