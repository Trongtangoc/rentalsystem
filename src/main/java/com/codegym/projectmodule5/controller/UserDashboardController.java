package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserDashboardController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        log.info("User dashboard accessed by: {}", authentication.getName());

        try {
            // Get user info
            var userInfo = userService.getUserProfile(authentication.getName());
            model.addAttribute("user", userInfo);

            // For now, we'll add empty lists for bookings and reviews
            // These will be populated when the services are ready
            model.addAttribute("bookings", java.util.Collections.emptyList());
            model.addAttribute("reviews", java.util.Collections.emptyList());

            log.info("User dashboard loaded successfully for: {}", authentication.getName());
            return "user-dashboard";

        } catch (Exception e) {
            log.error("Error loading user dashboard for: {}", authentication.getName(), e);
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());

            // Create a default user object to prevent template errors
            var defaultUser = new com.codegym.projectmodule5.dto.response.UserInfoResponse();
            defaultUser.setUsername(authentication.getName());
            defaultUser.setRole("ROLE_USER");
            defaultUser.setTotalBookings(0);
            defaultUser.setTotalReviews(0);
            model.addAttribute("user", defaultUser);
            model.addAttribute("bookings", java.util.Collections.emptyList());
            model.addAttribute("reviews", java.util.Collections.emptyList());

            return "user-dashboard";
        }
    }
}