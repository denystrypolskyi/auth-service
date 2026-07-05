package com.example.auth_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.dto.AuthUserResponseDTO;
import com.example.auth_service.dto.LoginRequestDTO;
import com.example.auth_service.dto.RegisterRequestDTO;
import com.example.auth_service.service.AuthUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;

    @GetMapping("/all")
    public ResponseEntity<List<AuthUserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(authUserService.getAllUsers().stream()
                .map(AuthUserResponseDTO::from)
                .toList());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthUserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthUserResponseDTO.from(authUserService.register(registerRequest)));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authUserService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authUserService.refreshToken(request.get("refreshToken")));
    }

}
