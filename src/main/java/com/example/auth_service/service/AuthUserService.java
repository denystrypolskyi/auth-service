package com.example.auth_service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.jwt.JwtUtil;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<AuthUser> getAllUsers() {
        return authUserRepository.findAll();
    }

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
        authUser.setEmail(dto.getEmail());

        return authUserRepository.save(authUser);
    }

    public Map<String, String> login(String username, String password) {
        AuthUser user = authUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(username, user.getEmail(), user.getId());
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

        String newAccessToken = jwtUtil.generateToken(username, user.getEmail(), user.getId());
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        user.setRefreshToken(newRefreshToken);
        authUserRepository.save(user);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return tokens;
    }
}
