package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.response.UserInfoResponse;
import com.codegym.projectmodule5.service.BookingService;
import com.codegym.projectmodule5.service.ReviewService;
import com.codegym.projectmodule5.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserDashboardController {

    private final UserService userService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        log.info("=== User Dashboard Access ===");
        log.info("Username: {}", authentication.getName());
        log.info("Authorities: {}", authentication.getAuthorities());

        try {
            // Get user info
            UserInfoResponse userInfo = userService.getUserProfile(authentication.getName());
            model.addAttribute("user", userInfo);
            log.info("User info loaded: username={}, role={}", userInfo.getUsername(), userInfo.getRole());

            // Get bookings
            try {
                var bookings = bookingService.getMyBookings(authentication.getName());
                model.addAttribute("bookings", bookings);
                log.info("Loaded {} bookings for user", bookings.size());
            } catch (Exception e) {
                log.warn("Could not load bookings: {}", e.getMessage());
                model.addAttribute("bookings", Collections.emptyList());
            }

            // Get reviews
            try {
                var reviews = reviewService.getMyReviews(authentication.getName());
                model.addAttribute("reviews", reviews);
                log.info("Loaded {} reviews for user", reviews.size());
            } catch (Exception e) {
                log.warn("Could not load reviews: {}", e.getMessage());
                model.addAttribute("reviews", Collections.emptyList());
            }

            log.info("User dashboard loaded successfully for: {}", authentication.getName());
            return "user-dashboard";

        } catch (Exception e) {
            log.error("Error loading user dashboard for: {}", authentication.getName(), e);
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());

            // Create a default user object to prevent template errors
            UserInfoResponse defaultUser = UserInfoResponse.builder()
                    .username(authentication.getName())
                    .role("ROLE_USER")
                    .totalBookings(0)
                    .totalReviews(0)
                    .totalHouses(0)
                    .build();

            model.addAttribute("user", defaultUser);
            model.addAttribute("bookings", Collections.emptyList());
            model.addAttribute("reviews", Collections.emptyList());

            return "user-dashboard";
        }
    }
}