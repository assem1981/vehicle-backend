package com.vehicle.backend.modules.messaging.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long bookingId;
    private String content;
    private boolean isRead;
    private LocalDateTime timestamp;
}
