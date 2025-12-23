package com.bus_tracker.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        // TODO: Implement actual JWT authentication logic
        LoginResponse response = new LoginResponse();
        response.setAccessToken("mock-jwt-token");
        response.setRole("STUDENT");
        response.setName("Mock User");
        return response;
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String accessToken;
        private String role;
        private String name;
    }
}
