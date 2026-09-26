package com.example.boilerplate.auth;

import com.example.boilerplate.auth.dto.AuthTokensResponse;
import com.example.boilerplate.auth.dto.LoginRequest;
import com.example.boilerplate.auth.dto.RegisterRequest;
import com.example.boilerplate.auth.dto.RefreshTokenRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
  @Autowired MockMvc mvc;

  

  @MockBean com.example.boilerplate.auth.JwtService jwtService;

  @Test
  void register_returnsCreatedAndTokens() throws Exception {
    var resp = new AuthTokensResponse("access","Bearer",900L,"refresh",2592000L);
    when(service.register(any(RegisterRequest.class))).thenReturn(resp);

    mvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"me@example.com\",\"password\":\"pw\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").value("access"))
        .andExpect(jsonPath("$.refreshToken").value("refresh"));
  }

  @Test
  void login_returnsTokens() throws Exception {
    var resp = new AuthTokensResponse("acc","Bearer",900L,"ref",2592000L);
    when(service.login(any(LoginRequest.class))).thenReturn(resp);

    mvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"me@example.com\",\"password\":\"pw\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("acc"));
  }

  @Test
  void refresh_rotates() throws Exception {
    var resp = new AuthTokensResponse("a","Bearer",900L,"r",2592000L);
    when(service.refresh(anyString())).thenReturn(resp);

    mvc.perform(post("/api/v1/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"r\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("a"));
  }

  @Test
  void logout_returnsNoContent() throws Exception {
    doNothing().when(service).logout(anyString());

    mvc.perform(post("/api/v1/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"r\"}"))
        .andExpect(status().isNoContent());
  }
}

