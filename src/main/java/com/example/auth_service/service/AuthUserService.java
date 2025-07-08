package com.example.auth_service.service;

import com.example.auth_service.dto.AuthUserDTO;
import com.example.auth_service.jwt.JwtUtil;
import com.example.auth_service.model.AuthUser;
import com.example.auth_service.repository.AuthUserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthUserService(AuthUserRepository authUserRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<AuthUser> findByUsername(String username) {
        return authUserRepository.findByUsername(username);
    }

    public AuthUser register(AuthUserDTO dto) {
        if (authUserRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        AuthUser authUser = new AuthUser();
        authUser.setUsername(dto.getUsername());
        authUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        return authUserRepository.save(authUser);
    }

    public String login(String username, String password) {
        Optional<AuthUser> user = authUserRepository.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return jwtUtil.generateToken(username);
        }

        throw new RuntimeException("Invalid credentials");
    }
}
