package com.auth_service.dto;

import lombok.*;

// Core Java → Data types, Strings
// OOPS → Encapsulation
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
    private String role; // ROLE_USER / ROLE_RESTAURANT_ADMIN
}