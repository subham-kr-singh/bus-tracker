package com.bus_tracker.controller;

import com.bus_tracker.dto.LoginRequest;
import com.bus_tracker.dto.LoginResponse;
import com.bus_tracker.entity.User;
import com.bus_tracker.repository.UserRepository;
import com.bus_tracker.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // In production, use passwordEncoder.matches(request.getPassword(),
        // user.getPasswordHash())
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token, user.getRole()));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> register(@RequestBody User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }
}
