package com.example.auth_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.auth_service.dto.UserEmailDTO;

@Service
public class UserClient {
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Optional<String> getUserEmailByUsername(String username) {
        try {
            UserEmailDTO user = restTemplate.getForObject(userServiceUrl + "/users/by-username/" + username,
                    UserEmailDTO.class);
            if (user == null) {
                System.err.println("User service returned null for username: " + username);
                return Optional.empty();
            }
            return Optional.ofNullable(user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to fetch user email for username " + username + ": " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
