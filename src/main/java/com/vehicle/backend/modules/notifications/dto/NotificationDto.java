package com.vehicle.backend.modules.notifications.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String recipientEmail;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
