package com.auth_service.service;

import com.auth_service.dto.*;
import com.auth_service.entity.AuthUser;
import com.auth_service.exception.UserNotFoundException;
import com.auth_service.repository.AuthUserRepository;
import com.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service // Service Layer Design (MVC)
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository repository; // DI
    private final JwtUtil jwtUtil;               // DI

    public String register(RegisterRequest request) {
        repository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserNotFoundException("Email already registered");
                });
        // Optional + Functional Programming + Exception Handling

        AuthUser user = AuthUser.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        repository.save(user); // Hibernate ORM
        return "User Registered";
    }

    public LoginResponse login(LoginRequest request) {

        AuthUser user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // Optional + Exception Handling

        if (!user.getPassword().equals(request.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(),user.getId());

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .email(user.getEmail())
                .build();
    }
}