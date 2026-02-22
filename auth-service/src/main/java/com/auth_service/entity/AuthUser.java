package com.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // Hibernate ORM → Entity Mapping
@Table(name = "auth_users") // Database & ORM
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Core Java → Data type (Long)

    @Column(unique = true)
    private String email;   // Core Java → String
    private String password; // Core Java → String
    private String role;    // Role-Based Authorization
}