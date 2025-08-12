// Fixed UserController.java

package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.ChangePasswordRequest;
import com.codegym.projectmodule5.dto.request.UpdateProfileRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.UserInfoResponse;
import com.codegym.projectmodule5.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserInfoResponse> getUserProfile(Authentication authentication) {
        try {
            UserInfoResponse response = userService.getUserProfile(authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserInfoResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        try {
            UserInfoResponse response = userService.updateProfile(request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            userService.changePassword(request, authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        try {
            List<UserInfoResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            userService.deleteUser(userId, authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{userId}/promote-to-host")
    public ResponseEntity<ApiResponse> promoteToHost(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            userService.promoteToHost(userId, authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "User promoted to host successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    // Fixed upgrade-to-host method - delegate to UserService
    @PutMapping("/upgrade-to-host")
    public ResponseEntity<ApiResponse> upgradeToHost(Authentication authentication) {
        try {
            log.info("User {} requesting upgrade to host", authentication.getName());

            // Delegate to UserService instead of direct repository access
            userService.upgradeCurrentUserToHost(authentication.getName());

            return ResponseEntity.ok(new ApiResponse(true,
                    "Successfully upgraded to Host! Please log out and log in again to access host features."));

        } catch (Exception e) {
            log.error("Error upgrading user to host: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to upgrade to host: " + e.getMessage()));
        }
    }
}