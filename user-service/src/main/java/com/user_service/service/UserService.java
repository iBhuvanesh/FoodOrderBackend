package com.user_service.service;

import com.user_service.dto.UserRequest;
import com.user_service.entity.User;
import com.user_service.exception.UserNotFoundException;
import com.user_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors; // Stream API

@Service // SpringBoot Configuration + Service Layer Design
@RequiredArgsConstructor // Dependency Injection (Constructor-based DI)
public class UserService {

    // Logger Implementation (Advanced Java)
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Inversion of Control (IOC)
    // Spring injects dependency automatically
    private final UserRepository userRepository;

    // ===============================
    // CREATE USER
    // ===============================
    public User createUser(UserRequest request) {

        logger.info("Creating new user with email: {}", request.getEmail());

        // OOPS - Builder Pattern (Abstraction + Encapsulation)
        User user = User.builder()
                .name(request.getName()) // Data Types + Strings
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        return userRepository.save(user); // Hibernate ORM + MySQL
    }

    // ===============================
    // GET USER BY ID
    // ===============================
    public User getUserById(Long id) {

        // Optional usage (Core Java 8)
        return userRepository.findById(id)
                .orElseThrow(() ->
                        // Exception Handling
                        new UserNotFoundException("User not found with ID: " + id)
                );
    }

    // ===============================
    // GET ALL USERS
    // ===============================
    public List<User> getAllUsers() {

        List<User> users = userRepository.findAll();
        // Collections Framework (List)

        // Stream API + Functional Programming
        return users.stream()
                .filter(user -> user.getRole() != null) // Lambda Expression
                .collect(Collectors.toList());
    }

    // ===============================
    // UPDATE USER
    // ===============================
    public User updateUser(Long id, UserRequest request) {

        User user = getUserById(id);

        // Encapsulation (Using setters instead of direct access)
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    // ===============================
    // DELETE USER
    // ===============================
    public void deleteUser(Long id) {

        User user = getUserById(id);

        userRepository.delete(user);
    }
}