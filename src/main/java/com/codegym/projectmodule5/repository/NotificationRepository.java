package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(long receiverId);
}
