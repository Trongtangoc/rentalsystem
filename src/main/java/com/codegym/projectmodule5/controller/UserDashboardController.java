package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.service.BookingService;
import com.codegym.projectmodule5.service.ReviewService;
import com.codegym.projectmodule5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserDashboardController {

    private final UserService userService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        try {
            var userInfo = userService.getUserProfile(authentication.getName());
            var bookings = bookingService.getMyBookings(authentication.getName());
            var reviews = reviewService.getMyReviews(authentication.getName());

            model.addAttribute("user", userInfo);
            model.addAttribute("bookings", bookings);
            model.addAttribute("reviews", reviews);

            return "user-dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}