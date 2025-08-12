// DebugController.java - For testing endpoints
package com.codegym.projectmodule5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class DebugController {

    @GetMapping("/debug/status")
    public Map<String, Object> getStatus() {
        log.info("Debug status endpoint called");
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Application is running");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }

    @GetMapping("/debug/auth")
    public Map<String, Object> getAuthStatus() {
        log.info("Debug auth endpoint called");
        Map<String, Object> status = new HashMap<>();
        status.put("loginUrl", "/auth/login");
        status.put("registerUrl", "/auth/register");
        status.put("apiLoginUrl", "/api/auth/login");
        status.put("message", "Auth endpoints available");
        return status;
    }
}