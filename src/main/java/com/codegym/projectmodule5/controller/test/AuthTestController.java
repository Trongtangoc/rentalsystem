package com.codegym.projectmodule5.controller.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.codegym.projectmodule5.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class AuthTestController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/manual-login")
    @ResponseBody
    public Map<String, Object> manualLogin(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("=== Manual login attempt ===");
            log.info("Username: {}", username);

            // Check if user exists
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                result.put("success", false);
                result.put("error", "User not found");
                return result;
            }

            var user = userOpt.get();
            result.put("userFound", true);
            result.put("userRole", user.getRole().getName().name());

            // Check password
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            result.put("passwordMatches", passwordMatches);

            if (!passwordMatches) {
                result.put("success", false);
                result.put("error", "Invalid password");
                return result;
            }

            // Try to authenticate
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            result.put("success", true);
            result.put("authenticated", authentication.isAuthenticated());
            result.put("authorities", authentication.getAuthorities().toString());
            result.put("message", "Login successful! You can now access protected pages.");
            result.put("redirectUrl", determineRedirectUrl(authentication));

            log.info("Manual login successful for user: {}", username);

        } catch (Exception e) {
            log.error("Manual login failed", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    private String determineRedirectUrl(Authentication auth) {
        var authorities = auth.getAuthorities();
        for (var authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "/admin/dashboard";
            } else if (role.equals("ROLE_HOST")) {
                return "/host/dashboard";
            } else if (role.equals("ROLE_USER")) {
                return "/user/dashboard";
            }
        }
        return "/dashboard";
    }

    @GetMapping("/test-auth-page")
    public String testAuthPage() {
        return "test-auth"; // We'll create this view
    }
}