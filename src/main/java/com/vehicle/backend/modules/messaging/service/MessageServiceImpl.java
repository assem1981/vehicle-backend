package com.vehicle.backend.modules.messaging.service;

import com.vehicle.backend.modules.messaging.dto.MessageDto;
import com.vehicle.backend.modules.messaging.entity.Message;
import com.vehicle.backend.modules.messaging.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;

    @Override
    public MessageDto sendMessage(Long senderId, Long receiverId, Long bookingId, String content) {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .bookingId(bookingId)
                .content(content)
                .isRead(false)
                .build();

        Message saved = repository.save(message);
        return mapToDto(saved);
    }

    @Override
    public List<MessageDto> getConversation(Long user1, Long user2) {
        return repository.findConversationBetweenUsers(user1, user2).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> getBookingMessages(Long bookingId) {
        return repository.findByBookingIdOrderByTimestampAsc(bookingId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private MessageDto mapToDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .bookingId(message.getBookingId())
                .content(message.getContent())
                .isRead(message.isRead())
                .timestamp(message.getTimestamp())
                .build();
    }
}
