package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.HostRegistrationRequestDto;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.HostRegistrationResponseDto;
import com.codegym.projectmodule5.service.HostRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HostRegistrationController {

    private final HostRegistrationService hostRegistrationService;

    // Show registration form
    @GetMapping("/host/register")
    public String showHostRegistrationForm(Model model, Authentication authentication) {
        log.info("User {} accessing host registration form", authentication.getName());

        // Check if user already has a request
        HostRegistrationResponseDto existingRequest = hostRegistrationService.getMyRequest(authentication.getName());
        if (existingRequest != null) {
            model.addAttribute("existingRequest", existingRequest);
        }

        model.addAttribute("username", authentication.getName());
        return "host-registration";
    }

    // Submit registration request (API)
    @PostMapping("/api/host/register")
    @ResponseBody
    public ResponseEntity<?> submitHostRegistration(
            @Valid @RequestBody HostRegistrationRequestDto dto,
            Authentication authentication) {
        try {
            log.info("Host registration submitted by user: {}", authentication.getName());
            HostRegistrationResponseDto response = hostRegistrationService.submitHostRequest(dto, authentication.getName());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Your host registration request has been submitted successfully! An admin will review it soon.",
                    "data", response
            ));
        } catch (Exception e) {
            log.error("Error submitting host registration", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // Admin view all pending requests
    @GetMapping("/admin/host-requests")
    public String viewHostRequests(Model model, Authentication authentication) {
        log.info("Admin {} viewing host requests", authentication.getName());
        List<HostRegistrationResponseDto> requests = hostRegistrationService.getAllPendingRequests(authentication.getName());
        model.addAttribute("requests", requests);
        return "admin-host-requests";
    }

    // Admin approve request (API)
    @PostMapping("/api/admin/host-requests/{id}/approve")
    @ResponseBody
    public ResponseEntity<ApiResponse> approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        try {
            hostRegistrationService.approveRequest(id, authentication.getName(), notes);
            return ResponseEntity.ok(new ApiResponse(true, "Request approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Admin reject request (API)
    @PostMapping("/api/admin/host-requests/{id}/reject")
    @ResponseBody
    public ResponseEntity<ApiResponse> rejectRequest(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        try {
            hostRegistrationService.rejectRequest(id, authentication.getName(), reason);
            return ResponseEntity.ok(new ApiResponse(true, "Request rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Get my request status (API)
    @GetMapping("/api/host/my-request")
    @ResponseBody
    public ResponseEntity<?> getMyRequest(Authentication authentication) {
        HostRegistrationResponseDto request = hostRegistrationService.getMyRequest(authentication.getName());
        if (request != null) {
            return ResponseEntity.ok(request);
        }
        return ResponseEntity.ok(Map.of("message", "No request found"));
    }
}