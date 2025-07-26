package com.example.auth_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.auth_service.client.UserClient;
import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.jwt.JwtUtil;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final UserClient userClient;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public Optional<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    @Transactional
    public AuthUser register(RegisterRequestDTO dto) {
        if (authUserRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        AuthUser authUser = new AuthUser();
        authUser.setUsername(dto.getUsername());
        authUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        AuthUser savedUser = authUserRepository.save(authUser);

        try {
            Map<String, String> userPayload = new HashMap<>();
            userPayload.put("name", dto.getUsername());
            userPayload.put("email", dto.getEmail());

            restTemplate.postForEntity(userServiceUrl, userPayload, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user profile", e);
        }

        return savedUser;
    }

    public Map<String, String> login(String username, String password) {
        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String email = userClient.getUserEmailByUsername(user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to fetch user email"));
        String accessToken = jwtUtil.generateToken(username, email);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        user.setRefreshToken(refreshToken);
        authUserRepository.save(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public Map<String, String> refreshToken(String refreshToken) {
        String username = jwtUtil.extractUsername(refreshToken);
        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!refreshToken.equals(user.getRefreshToken()) || jwtUtil.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String email = userClient.getUserEmailByUsername(user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to fetch user email"));

        String newAccessToken = jwtUtil.generateToken(username, email);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        user.setRefreshToken(newRefreshToken);
        authUserRepository.save(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }
}
