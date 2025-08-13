package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class LoginTestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/check-admin")
    public Map<String, Object> checkAdminUser() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Check if admin user exists
            var adminOpt = userRepository.findByUsername("admin");

            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                result.put("adminExists", true);
                result.put("username", admin.getUsername());
                result.put("email", admin.getEmail());
                result.put("role", admin.getRole().getName().name());
                result.put("passwordEncoded", admin.getPassword() != null && admin.getPassword().startsWith("$2"));

                // Test password verification
                boolean passwordMatches = passwordEncoder.matches("admin123", admin.getPassword());
                result.put("passwordVerification", passwordMatches);

                log.info("Admin check result: {}", result);
            } else {
                result.put("adminExists", false);
                result.put("message", "Admin user not found in database");
            }

            // List all users
            var allUsers = userRepository.findAll();
            result.put("totalUsers", allUsers.size());
            result.put("users", allUsers.stream()
                    .map(u -> Map.of(
                            "id", u.getId(),
                            "username", u.getUsername(),
                            "role", u.getRole().getName().name()
                    ))
                    .toList());

        } catch (Exception e) {
            log.error("Error checking admin user", e);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @GetMapping("/test-encoding")
    public Map<String, Object> testPasswordEncoding() {
        Map<String, Object> result = new HashMap<>();

        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        result.put("rawPassword", rawPassword);
        result.put("encodedPassword", encodedPassword);
        result.put("verificationTest", passwordEncoder.matches(rawPassword, encodedPassword));

        return result;
    }
}