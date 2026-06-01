package com.academic.authservice.service;

import com.academic.authservice.dto.*;
import com.academic.authservice.model.User;
import com.academic.authservice.repository.UserRepository;
import com.academic.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : User.Role.STUDENT)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getRole().name());
    }
}