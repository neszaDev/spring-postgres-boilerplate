package com.example.boilerplate.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.boilerplate.model.User;
import com.example.boilerplate.repository.UserRepository;
import com.example.boilerplate.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
@WebMvcTest(AuthController.class)
public class AuthControllerLoginTest {
  @Autowired MockMvc mvc;

  @MockBean AuthService authService;

  @MockBean UserRepository userRepository;

  @Test
  void login_returns_tokens() throws Exception {
    User u = new User();
    u.setId(5L);
    u.setEmail("test@ex.com");
    when(authService.authenticate(anyString(), anyString())).thenReturn(u);
    when(authService.loginAccessToken(any(User.class), any(Long.class)))
        .thenReturn("access-token-abc");
    when(authService.createRefreshToken(any(User.class), any(Long.class)))
        .thenReturn("refresh-token-xyz");

    String body = "{\"email\":\"test@ex.com\",\"password\":\"pw\"}";
    mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-token-abc"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token-xyz"));
  }

  @Test
  void refresh_calls_rotate_and_returns_new_tokens() throws Exception {
    User u = new User();
    u.setId(6L);
    u.setEmail("r@ex.com");
    when(authService.validateRefreshToken(anyString())).thenReturn(u);
    when(authService.loginAccessToken(any(User.class), any(Long.class))).thenReturn("new-access");
    when(authService.rotateRefreshToken(anyString(), any(Long.class))).thenReturn("new-refresh");

    String body = "{\"refreshToken\":\"old-token\"}";
    mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-access"))
        .andExpect(jsonPath("$.refreshToken").value("new-refresh"));

    verify(authService).validateRefreshToken("old-token");
  }

  @Test
  void logout_revokes_refresh_token() throws Exception {
    String body = "{\"refreshToken\":\"old-token\"}";
    mvc.perform(post("/auth/logout").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ok"));
    verify(authService).revokeRefreshToken("old-token");
  }
}
