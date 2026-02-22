package com.auth_service.service;

import com.auth_service.dto.*;
import com.auth_service.entity.AuthUser;
import com.auth_service.exception.UserNotFoundException;
import com.auth_service.repository.AuthUserRepository;
import com.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository repository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate; // ✅ inject RestTemplate

    @Value("${user-service.url:http://localhost:8081}")
    private String userServiceUrl;  // ✅ point to your user-service port

    public String register(RegisterRequest request) {
        repository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserNotFoundException("Email already registered");
                });

        AuthUser user = AuthUser.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        repository.save(user);

        // ✅ Sync new user to user-service so admin can see them
        try {
            Map<String, String> userPayload = Map.of(
                    "name",  request.getName() != null ? request.getName() : "",
                    "email", request.getEmail(),
                    "phone", request.getPhone() != null ? request.getPhone() : "",
                    "role",  request.getRole()
            );
            restTemplate.postForObject(userServiceUrl + "/users", userPayload, String.class);
        } catch (Exception e) {
            // Log but don't fail registration if user-service is down
            System.err.println("Warning: Failed to sync user to user-service: " + e.getMessage());
        }

        return "User Registered";
    }

    public LoginResponse login(LoginRequest request) {
        AuthUser user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }
}