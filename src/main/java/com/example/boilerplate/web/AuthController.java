package com.example.boilerplate.web;

import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.UserRepository;
import com.example.boilerplate.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    public static record RegisterRequest(String email, String password, String fullName) {}
    public static record LoginRequest(String email, String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User u = authService.register(req.email(), req.password(), req.fullName());
        return ResponseEntity.ok(Map.of("id", u.getId(), "email", u.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) throws Exception {
        User u = authService.authenticate(req.email(), req.password());
        String access = authService.loginAccessToken(u, 15 * 60 * 1000);
        String refresh = authService.createRefreshToken(u, 30);
        return ResponseEntity.ok(Map.of("accessToken", access, "refreshToken", refresh));
    }

