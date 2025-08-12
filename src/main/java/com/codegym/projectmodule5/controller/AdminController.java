//package com.codegym.projectmodule5.controller;
//
//import com.codegym.projectmodule5.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/admin")
//public class AdminController {
//
//    private final UserService userService;
//
//    @GetMapping("/dashboard")
//    public String dashboard(Model model, Authentication authentication) {
//        try {
//            var allUsers = userService.getAllUsers();
//            var adminInfo = userService.getUserProfile(authentication.getName());
//
//            model.addAttribute("users", allUsers);
//            model.addAttribute("admin", adminInfo);
//
//            return "admin-dashboard";
//        } catch (RuntimeException e) {
//            model.addAttribute("error", e.getMessage());
//            return "error";
//        }
//    }
//}
// Simple AdminController.java
package com.codegym.projectmodule5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("Admin dashboard accessed");
        model.addAttribute("message", "Welcome to Admin Dashboard!");
        return "admin-dashboard";
    }
}