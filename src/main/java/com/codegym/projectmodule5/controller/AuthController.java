package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.LoginRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                HttpServletRequest request) {
        log.info("=== Login form accessed ===");
        log.info("Error param: {}", error);
        log.info("Logout param: {}", logout);

        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            log.info("User already authenticated: {}, redirecting to dashboard", auth.getName());
            return "redirect:/dashboard";
        }

        // Check for authentication error in session
        HttpSession session = request.getSession(false);
        if (session != null) {
            Exception ex = (Exception) session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (ex != null) {
                log.error("Login error from session: {}", ex.getMessage());
                model.addAttribute("error", "Invalid username or password: " + ex.getMessage());
                session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            }
        }

        if (error != null) {
            log.warn("Login failed - error parameter present");
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            log.info("User logged out successfully");
            model.addAttribute("success", "You have been logged out successfully");
        }

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login-debug")
    @ResponseBody
    public String debugLogin(@RequestParam String username, @RequestParam String password) {
        log.info("=== Debug login attempt ===");
        log.info("Username: {}", username);
        log.info("Password length: {}", password.length());

        try {
            // This is just for debugging, not actual login
            return "Debug: Login form is being submitted. Check server logs.";
        } catch (Exception e) {
            log.error("Debug login error", e);
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.info("Showing register form");
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") RegisterRequest request,
                                 BindingResult bindingResult, Model model) {
        log.info("Registration attempt for user: {}", request.getUsername());

        if (bindingResult.hasErrors()) {
            log.warn("Registration validation errors: {}", bindingResult.getAllErrors());
            return "register";
        }

        try {
            authService.register(request);
            log.info("Registration successful for user: {}", request.getUsername());
            model.addAttribute("success", "Registration successful! Please login with your credentials.");

            // Redirect to login page after successful registration
            return "redirect:/auth/login?registered=true";
        } catch (Exception e) {
            log.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/check")
    @ResponseBody
    public String checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return "Authenticated as: " + auth.getName() + " with authorities: " + auth.getAuthorities();
        }
        return "Not authenticated";
    }
}