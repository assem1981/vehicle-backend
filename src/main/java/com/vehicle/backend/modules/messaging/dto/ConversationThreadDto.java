package com.vehicle.backend.modules.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationThreadDto {
    private String id;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatar;
    private String vehicleName;
    private Long bookingId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean unread;
}
