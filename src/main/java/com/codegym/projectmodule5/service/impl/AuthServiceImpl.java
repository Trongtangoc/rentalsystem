package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.LoginRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.response.JwtResponse;
import com.codegym.projectmodule5.entity.Role;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        // Validation
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email is already taken");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException("Phone is already taken");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Passwords do not match");
        }

        // Get default USER role
        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new CustomException("User Role not found"));

        // Create user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .emailVerified(false) // <- rõ ràng
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .enabled(true)           // Explicitly set if desired
                .accountExpired(false)   // Explicitly set if desired
                .accountLocked(false)    // Explicitly set if desired
                .credentialsExpired(false) // Explicitly set if desired
                .build();

        userRepository.save(user);

        log.info("User {} registered successfully", request.getUsername());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Get user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new CustomException("User not found"));

            // Return simple response (we'll add JWT later)
            return JwtResponse.builder()
                    .token("simple-session-token") // Temporary token
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().getName().name())
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new CustomException("Invalid username or password");
        }
    }

    @Override
    public void logout(String token) {
        log.info("User logged out");
        // For now, just log. Later we can implement token blacklisting
    }
}