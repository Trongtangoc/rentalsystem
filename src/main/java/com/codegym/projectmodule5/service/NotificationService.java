package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.response.NotificationResponse;
import com.codegym.projectmodule5.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void createNotification(String message, NotificationType type, Long receiverId);
    List<NotificationResponse> getMyNotifications(String username);
    void markAsRead(Long notificationId, String username);
    void markAllAsRead(String username);
    long getUnreadCount(String username);
}
