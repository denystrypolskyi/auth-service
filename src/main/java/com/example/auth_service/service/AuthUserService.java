package com.example.auth_service.service;

import com.example.auth_service.dto.AuthUserDTO;
import com.example.auth_service.jwt.JwtUtil;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    private final String userServiceUrl = "http://localhost:8081/users";

    public AuthUserService(AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;

    }

    public Optional<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    @Transactional
    public AuthUser register(AuthUserDTO dto) {
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

    public String login(String username, String password) {
        Optional<AuthUser> user = authUserRepository.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return jwtUtil.generateToken(username);
        }

        throw new RuntimeException("Invalid credentials");
    }
}
