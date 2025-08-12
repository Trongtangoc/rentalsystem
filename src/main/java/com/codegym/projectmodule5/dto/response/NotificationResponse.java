package com.codegym.projectmodule5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private LocalDateTime createdAt;
    private boolean isRead;
    private Long receiverId;
}