package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            var userInfo = userService.getUserProfile(authentication.getName());
            model.addAttribute("user", userInfo);

            // Redirect based on role
            String role = userInfo.getRole();
            switch (role) {
                case "ROLE_ADMIN":
                    return "redirect:/admin/dashboard";
                case "ROLE_HOST":
                    return "redirect:/host/dashboard";
                default:
                    return "redirect:/user/dashboard";
            }
        } catch (Exception e) {
            log.error("Error loading dashboard for user: {}", authentication.getName(), e);
            model.addAttribute("error", "Error loading dashboard");
            return "error";
        }
    }
}