package com.codegym.projectmodule5.controller.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class TestAccessController {

    @GetMapping("/test/user-access")
    public String testUserAccess(Authentication authentication, Model model) {
        log.info("User access test - User: {}, Authorities: {}",
                authentication.getName(),
                authentication.getAuthorities());

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("message", "If you can see this, you are logged in as a USER!");

        return "test-access"; // We'll create a simple view for this
    }

    @GetMapping("/test/host-access")
    public String testHostAccess(Authentication authentication, Model model) {
        log.info("Host access test - User: {}, Authorities: {}",
                authentication.getName(),
                authentication.getAuthorities());

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("message", "If you can see this, you are logged in as a HOST!");

        return "test-access";
    }

    @GetMapping("/test/admin-access")
    public String testAdminAccess(Authentication authentication, Model model) {
        log.info("Admin access test - User: {}, Authorities: {}",
                authentication.getName(),
                authentication.getAuthorities());

        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("message", "If you can see this, you are logged in as an ADMIN!");

        return "test-access";
    }
}