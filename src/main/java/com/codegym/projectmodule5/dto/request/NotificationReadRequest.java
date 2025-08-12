package com.codegym.projectmodule5.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationReadRequest {
    @NotNull(message = "Notification ID is required")
    private Long notificationId;
}