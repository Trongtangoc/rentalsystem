package com.codegym.projectmodule5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
@Slf4j
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        log.info("=== Main Dashboard Access ===");
        log.info("User: {}", authentication.getName());
        log.info("Authorities: {}", authentication.getAuthorities());

        try {
            // Get the user's authorities
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // Check roles and redirect accordingly
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                log.info("Checking role: {}", role);

                if ("ROLE_ADMIN".equals(role)) {
                    log.info("User is ADMIN - Redirecting to admin dashboard");
                    return "redirect:/admin/dashboard";
                }
                if ("ROLE_HOST".equals(role)) {
                    log.info("User is HOST - Redirecting to host dashboard");
                    return "redirect:/host/dashboard";
                }
                if ("ROLE_USER".equals(role)) {
                    log.info("User is USER - Redirecting to user dashboard");
                    return "redirect:/user/dashboard";
                }
            }

            // Default fallback to user dashboard
            log.warn("No matching role found for user: {}, defaulting to user dashboard", authentication.getName());
            return "redirect:/user/dashboard";

        } catch (Exception e) {
            log.error("Error in dashboard controller for user: {}", authentication.getName(), e);
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }
}