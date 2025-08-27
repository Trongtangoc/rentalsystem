package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class UserTestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/all-users")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();

        try {
            var allUsers = userRepository.findAll();
            result.put("totalUsers", allUsers.size());
            result.put("users", allUsers.stream()
                    .map(u -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("id", u.getId());
                        userInfo.put("username", u.getUsername());
                        userInfo.put("email", u.getEmail());
                        userInfo.put("phone", u.getPhone());
                        userInfo.put("role", u.getRole().getName().name());
                        return userInfo;
                    })
                    .collect(Collectors.toList()));

            // Test passwords for known test accounts
            Map<String, Boolean> passwordTests = new HashMap<>();

            userRepository.findByUsername("admin").ifPresent(u ->
                    passwordTests.put("admin/admin123", passwordEncoder.matches("admin123", u.getPassword())));

            userRepository.findByUsername("host").ifPresent(u ->
                    passwordTests.put("host/host123", passwordEncoder.matches("host123", u.getPassword())));

            userRepository.findByUsername("user").ifPresent(u ->
                    passwordTests.put("user/user123", passwordEncoder.matches("user123", u.getPassword())));

            userRepository.findByUsername("john").ifPresent(u ->
                    passwordTests.put("john/john123", passwordEncoder.matches("john123", u.getPassword())));

            result.put("passwordVerification", passwordTests);

        } catch (Exception e) {
            log.error("Error getting users", e);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            result.put("authenticated", true);
            result.put("username", authentication.getName());
            result.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            result.put("principal", authentication.getPrincipal().toString());
        } else {
            result.put("authenticated", false);
            result.put("message", "No authenticated user");
        }

        return result;
    }

    @PostMapping("/test-login")
    public Map<String, Object> testLogin(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            var userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                var user = userOpt.get();
                boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

                result.put("userFound", true);
                result.put("username", user.getUsername());
                result.put("role", user.getRole().getName().name());
                result.put("passwordMatches", passwordMatches);

                if (!passwordMatches) {
                    result.put("hint", "Password doesn't match. Make sure you're using the correct password.");
                }
            } else {
                result.put("userFound", false);
                result.put("message", "User not found with username: " + username);
            }

        } catch (Exception e) {
            log.error("Error testing login", e);
            result.put("error", e.getMessage());
        }

        return result;
    }
}