package com.example.auth_service.dto;

import com.example.auth_service.model.AuthUser;

public record AuthUserResponseDTO(Long id, String username, String email) {
    public static AuthUserResponseDTO from(AuthUser user) {
        return new AuthUserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
