package com.example.boilerplate.web;

import com.example.boilerplate.model.User;
import com.example.boilerplate.service.AuthService;
import com.example.boilerplate.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
@WebMvcTest(AuthController.class)
public class AuthControllerNegativeTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AuthService authService;

    @MockBean
    UserRepository userRepository;

    @Test
    void login_invalid_credentials_returns_400() throws Exception {
        when(authService.authenticate(anyString(), anyString())).thenThrow(new IllegalArgumentException("Invalid credentials"));

        String body = "{\"email\":\"bad@ex.com\",\"password\":\"wrong\"}";
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_invalid_token_returns_400() throws Exception {
        when(authService.validateRefreshToken(anyString())).thenThrow(new IllegalArgumentException("Invalid or expired refresh token"));

        String body = "{\"refreshToken\":\"invalid\"}";
        mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
