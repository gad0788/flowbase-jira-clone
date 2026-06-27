package com.flowbase.jira.controller;

import com.flowbase.jira.dto.request.AuthRequest;
import com.flowbase.jira.dto.request.RegisterRequest;
import com.flowbase.jira.dto.response.AuthResponse;
import com.flowbase.jira.model.User;
import com.flowbase.jira.repository.UserRepository;
import com.flowbase.jira.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmailAddress(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        User user = new User(request.getDisplayName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmailAddress());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getId(), user.getDisplayName(), user.getEmailAddress()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        User user = userRepository.findByEmailAddress(request.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmailAddress());
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getDisplayName(), user.getEmailAddress()));
    }
}
