package com.example.boilerplate.user;

import com.example.boilerplate.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;`r`nimport com.example.boilerplate.security.JwtAuthenticationFilter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {`r`n  @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
  @Autowired MockMvc mvc;

  @MockBean UserService userService;

  @Test
  void unauthenticated_returns401() throws Exception {
    mvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "me@example.com")
  void authenticated_returnsUser() throws Exception {
    UserResponse ur = new UserResponse(1L, "me@example.com", null, Instant.now());
    when(userService.currentUser(anyString())).thenReturn(ur);

    mvc.perform(get("/api/v1/users/me")).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("me@example.com"));
  }
}

