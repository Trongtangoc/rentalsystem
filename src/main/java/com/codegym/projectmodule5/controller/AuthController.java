//package com.codegym.projectmodule5.controller;
//
//import com.codegym.projectmodule5.dto.request.LoginRequest;
//import com.codegym.projectmodule5.dto.request.RegisterRequest;
//import com.codegym.projectmodule5.dto.response.JwtResponse;
//import com.codegym.projectmodule5.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Controller
//@RequestMapping("/auth")
//@Slf4j
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @GetMapping("/login")
//    public String showLoginForm(Model model) {
//        log.info("Showing login form");
//        model.addAttribute("loginRequest", new LoginRequest());
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String handleLogin(@ModelAttribute("loginRequest") LoginRequest request,
//                              Model model,
//                              HttpServletResponse response) {
//        log.info("Login attempt for user: {}", request.getUsername());
//
//        try {
//            // Sử dụng AuthService để login với JWT
//            JwtResponse jwtResponse = authService.login(request);
//
//            // Lưu token vào cookie
//            Cookie cookie = new Cookie("authToken", jwtResponse.getToken());
//            cookie.setHttpOnly(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(86400); // 24 hours
//            response.addCookie(cookie);
//
//            // Redirect dựa trên role
//            String role = jwtResponse.getRole();
//            log.info("Login successful for user: {} with role: {}", request.getUsername(), role);
//
//            if ("ROLE_ADMIN".equals(role)) {
//                return "redirect:/admin/dashboard";
//            } else if ("ROLE_HOST".equals(role)) {
//                return "redirect:/host/dashboard";
//            } else {
//                return "redirect:/user/dashboard";
//            }
//
//        } catch (Exception e) {
//            log.error("Login failed: {}", e.getMessage());
//            model.addAttribute("error", "Invalid username or password");
//            return "login";
//        }
//    }
//
//    @GetMapping("/register")
//    public String showRegisterForm(Model model) {
//        log.info("Showing register form");
//        model.addAttribute("user", new RegisterRequest());
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String handleRegister(@ModelAttribute("user") RegisterRequest request, Model model) {
//        log.info("Registration attempt for user: {}", request.getUsername());
//
//        try {
//            authService.register(request);
//            model.addAttribute("success", "Registration successful! Please login.");
//            model.addAttribute("loginRequest", new LoginRequest());
//            return "login";
//        } catch (Exception e) {
//            log.error("Registration failed: {}", e.getMessage());
//            model.addAttribute("error", e.getMessage());
//            return "register";
//        }
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        // Clear the auth cookie
//        Cookie cookie = new Cookie("authToken", null);
//        cookie.setPath("/");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//
//        return "redirect:/auth/login";
//    }
//}

package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.LoginRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        log.info("Showing login form");
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequest request, Model model) {
        log.info("Login attempt for user: {}", request.getUsername());

        try {
            // Đơn giản hóa để test
            if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
                log.info("Admin login successful");
                return "redirect:/admin/dashboard";
            }

            // Thử login thông thường
            authService.login(request);
            return "redirect:/user/dashboard";

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.info("Showing register form");
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") RegisterRequest request, Model model) {
        log.info("Registration attempt for user: {}", request.getUsername());

        try {
            authService.register(request);
            model.addAttribute("success", "Registration successful! Please login.");
            model.addAttribute("loginRequest", new LoginRequest());
            return "login";
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/auth/login";
    }
}