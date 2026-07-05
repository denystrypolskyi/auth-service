package com.example.auth_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.jwt.JwtUtil;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.service.AuthUserService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUserService authUserService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void registerReturnsSafeUserResponse() throws Exception {
        AuthUser user = new AuthUser(1L, "demo", "demo@example.com", "hashed-password", "refresh-token");
        when(authUserService.register(any(RegisterRequestDTO.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "demo",
                          "password": "password",
                          "email": "demo@example.com"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("demo"))
                .andExpect(jsonPath("$.email").value("demo@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
    }
}
