package com.vehicle.backend.modules.messaging.controller;

import com.vehicle.backend.modules.messaging.dto.ConversationThreadDto;
import com.vehicle.backend.modules.messaging.dto.MessageDto;
import com.vehicle.backend.modules.messaging.service.MessageService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam String content) {
        Long senderId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(messageService.sendMessage(senderId, receiverId, bookingId, content));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageDto>> getConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long otherUserId) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(messageService.getConversation(userId, otherUserId));
    }

    @GetMapping("/threads")
    public ResponseEntity<List<ConversationThreadDto>> getUserThreads(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(messageService.getUserThreads(userId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<MessageDto>> getBookingMessages(@PathVariable Long bookingId) {
        return ResponseEntity.ok(messageService.getBookingMessages(bookingId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        long count = messageService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PostMapping("/read/{otherUserId}")
    public ResponseEntity<Map<String, String>> markAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long otherUserId) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        messageService.markConversationAsRead(userId, otherUserId);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}
