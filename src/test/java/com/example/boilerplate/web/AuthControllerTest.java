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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    AuthService authService;

    @MockBean
    UserRepository userRepository;

    @Test
    void register_returns_user_info() throws Exception {
        User u = new User();
        u.setId(123L);
        u.setEmail("test@example.com");
        when(authService.register(anyString(), anyString(), anyString())).thenReturn(u);

        String body = "{\"email\":\"test@example.com\",\"password\":\"secret\",\"fullName\":\"Test User\"}";

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
