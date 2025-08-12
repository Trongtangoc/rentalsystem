package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.service.BookingService;
import com.codegym.projectmodule5.service.HouseService;
import com.codegym.projectmodule5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/host")
public class HostController {

    private final UserService userService;
    private final HouseService houseService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        try {
            var userInfo = userService.getUserProfile(authentication.getName());
            var houses = houseService.getMyHouses(authentication.getName());
            var bookings = bookingService.getBookingsForMyHouses(authentication.getName());

            model.addAttribute("user", userInfo);
            model.addAttribute("houses", houses);
            model.addAttribute("bookings", bookings);

            return "host-dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
