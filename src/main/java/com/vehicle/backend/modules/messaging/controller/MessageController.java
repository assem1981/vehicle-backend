package com.vehicle.backend.modules.messaging.controller;

import com.vehicle.backend.modules.messaging.dto.MessageDto;
import com.vehicle.backend.modules.messaging.service.MessageService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(messageService.sendMessage(userPrincipal.getId(), receiverId, bookingId, content));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageDto>> getConversation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long otherUserId) {
        return ResponseEntity.ok(messageService.getConversation(userPrincipal.getId(), otherUserId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<MessageDto>> getBookingMessages(@PathVariable Long bookingId) {
        return ResponseEntity.ok(messageService.getBookingMessages(bookingId));
    }
}
