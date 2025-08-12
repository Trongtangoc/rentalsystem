// AuthRestController.java - For API endpoints
package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.LoginRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.JwtResponse;
import com.codegym.projectmodule5.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("API registration request for user: {}", request.getUsername());
            authService.register(request);
            return ResponseEntity.ok(new ApiResponse(true, "Registration successful"));
        } catch (RuntimeException e) {
            log.error("API registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("API login request for user: {}", request.getUsername());
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("API login failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(401)
                    .body(JwtResponse.builder()
                            .token(null)
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            log.info("API logout request");
            if (token != null) {
                authService.logout(token);
            }
            return ResponseEntity.ok(new ApiResponse(true, "Logout successful"));
        } catch (RuntimeException e) {
            log.error("API logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}