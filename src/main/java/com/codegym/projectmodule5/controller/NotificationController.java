package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.NotificationResponse;
import com.codegym.projectmodule5.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication authentication) {
        try {
            List<NotificationResponse> notifications = notificationService.getMyNotifications(authentication.getName());
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {
        try {
            notificationService.markAsRead(notificationId, authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "Notification marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse> markAllAsRead(Authentication authentication) {
        try {
            notificationService.markAllAsRead(authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "All notifications marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        try {
            long count = notificationService.getUnreadCount(authentication.getName());
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(0L);
        }
    }
}