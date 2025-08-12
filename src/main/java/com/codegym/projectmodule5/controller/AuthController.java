//// Simple AuthController.java
//package com.codegym.projectmodule5.controller;
//
//import com.codegym.projectmodule5.dto.request.LoginRequest;
//import com.codegym.projectmodule5.dto.request.RegisterRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequestMapping("/auth")
//@Slf4j
//public class AuthController {
//
//    @GetMapping("/login")
//    public String showLoginForm(Model model) {
//        log.info("Showing login form");
//        model.addAttribute("loginRequest", new LoginRequest());
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String handleLogin(@ModelAttribute("loginRequest") LoginRequest request, Model model) {
//        log.info("Login attempt for user: {}", request.getUsername());
//
//        // Simple admin check for testing
//        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
//            log.info("Admin login successful");
//            return "redirect:/admin/dashboard";
//        }
//
//        model.addAttribute("error", "Invalid username or password");
//        return "login";
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
//        model.addAttribute("success", "Registration successful! Please login.");
//        return "register";
//    }
//
//    @GetMapping("/logout")
//    public String logout() {
//        return "redirect:/auth/login";
//    }
//}
// Simple AuthController.java
package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.LoginRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        log.info("Showing login form");
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("loginRequest") LoginRequest request, Model model) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Simple admin check for testing
        if ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword())) {
            log.info("Admin login successful");
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
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
        model.addAttribute("success", "Registration successful! Please login.");
        return "register";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/auth/login";
    }
}