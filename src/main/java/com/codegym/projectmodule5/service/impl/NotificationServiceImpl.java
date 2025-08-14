package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.response.NotificationResponse;
import com.codegym.projectmodule5.entity.Notification;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.NotificationType;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.exception.ResourceNotFoundException;
import com.codegym.projectmodule5.exception.UnauthorizedException;
import com.codegym.projectmodule5.repository.NotificationRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotification(String message, NotificationType type, Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .receiver(receiver)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId());

        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only mark your own notifications as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId());
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .filter(notification -> !notification.isRead())
                .count();
    }

    @Override

    public void notifyAdmins(String message) {
        // Find all admin users
        List<User> admins = userRepository.findAll().stream()
                .filter(user -> user.getRole().getName() == RoleEnum.ROLE_ADMIN)
                .collect(Collectors.toList());

        // Create notification for each admin
        for (User admin : admins) {
            createNotification(message, NotificationType.SYSTEM, admin.getId());
        }

        log.info("Notified {} admins: {}", admins.size(), message);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType().name())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .receiverId(notification.getReceiver().getId())
                .build();
    }
}