package com.vehicle.backend.modules.messaging.service;

import com.vehicle.backend.modules.messaging.dto.ConversationThreadDto;
import com.vehicle.backend.modules.messaging.dto.MessageDto;

import java.util.List;

public interface MessageService {
    MessageDto sendMessage(Long senderId, Long receiverId, Long bookingId, String content);
    List<MessageDto> getConversation(Long user1, Long user2);
    List<MessageDto> getBookingMessages(Long bookingId);
    List<ConversationThreadDto> getUserThreads(Long userId);
    long getUnreadCount(Long userId);
    void markConversationAsRead(Long currentUserId, Long otherUserId);
}
