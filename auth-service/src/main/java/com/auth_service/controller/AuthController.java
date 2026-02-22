package com.auth_service.controller;


import com.auth_service.dto.*;
import com.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController // REST API
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // DI

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}