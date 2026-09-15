package com.vehicle.backend.modules.messaging.service;

import com.vehicle.backend.modules.booking.entity.Booking;
import com.vehicle.backend.modules.booking.repository.BookingRepository;
import com.vehicle.backend.modules.messaging.dto.ConversationThreadDto;
import com.vehicle.backend.modules.messaging.dto.MessageDto;
import com.vehicle.backend.modules.messaging.entity.Message;
import com.vehicle.backend.modules.messaging.repository.MessageRepository;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public MessageDto sendMessage(Long senderId, Long receiverId, Long bookingId, String content) {
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .bookingId(bookingId)
                .content(content)
                .isRead(false)
                .build();

        Message saved = repository.save(message);
        MessageDto dto = mapToDto(saved);

        // Broadcast to WebSocket subscribers for real-time room instant messaging
        if (messagingTemplate != null) {
            try {
                // Topic for conversation between sender and receiver
                messagingTemplate.convertAndSend("/topic/messages", dto);
                messagingTemplate.convertAndSend("/topic/user." + receiverId + ".messages", dto);
                messagingTemplate.convertAndSend("/topic/conversation." + senderId + "." + receiverId, dto);
                messagingTemplate.convertAndSend("/topic/conversation." + receiverId + "." + senderId, dto);
            } catch (Exception e) {
                // WebSocket notification fallback
            }
        }

        return dto;
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

    @Override
    public List<ConversationThreadDto> getUserThreads(Long userId) {
        if (userId == null) userId = 1L;

        List<Message> allMessages = repository.findUserMessages(userId);

        Map<Long, Message> latestMessageMap = new LinkedHashMap<>();
        for (Message m : allMessages) {
            Long otherId = m.getSenderId().equals(userId) ? m.getReceiverId() : m.getSenderId();
            if (!latestMessageMap.containsKey(otherId)) {
                latestMessageMap.put(otherId, m);
            }
        }

        List<ConversationThreadDto> threads = new ArrayList<>();
        for (Map.Entry<Long, Message> entry : latestMessageMap.entrySet()) {
            Long otherUserId = entry.getKey();
            Message msg = entry.getValue();

            User otherUser = userRepository.findById(otherUserId).orElse(null);
            String name = "User #" + otherUserId;
            if (otherUser != null) {
                String fullName = ((otherUser.getFirstName() != null ? otherUser.getFirstName() : "") + " " +
                                   (otherUser.getLastName() != null ? otherUser.getLastName() : "")).trim();
                if (!fullName.isEmpty()) name = fullName;
                else if (otherUser.getEmail() != null) name = otherUser.getEmail();
            }

            String vehicleName = "Fleet Reservation";
            if (msg.getBookingId() != null) {
                Booking b = bookingRepository.findById(msg.getBookingId()).orElse(null);
                if (b != null && b.getVehicleId() != null) {
                    Vehicle v = vehicleRepository.findById(b.getVehicleId()).orElse(null);
                    if (v != null) {
                        vehicleName = v.getMake() + " " + v.getModel() + " (" + v.getYear() + ")";
                    }
                }
            }

            boolean unread = !msg.isRead() && msg.getReceiverId().equals(userId);

            threads.add(ConversationThreadDto.builder()
                    .id(String.valueOf(otherUserId))
                    .otherUserId(otherUserId)
                    .otherUserName(name)
                    .otherUserAvatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80")
                    .vehicleName(vehicleName)
                    .bookingId(msg.getBookingId())
                    .lastMessage(msg.getContent())
                    .lastMessageTime(msg.getTimestamp())
                    .unread(unread)
                    .build());
        }

        return threads;
    }

    @Override
    public long getUnreadCount(Long userId) {
        if (userId == null) return 0;
        return repository.countByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long currentUserId, Long otherUserId) {
        if (currentUserId == null || otherUserId == null) return;
        List<Message> unreadMessages = repository.findUnreadFromUser(otherUserId, currentUserId);
        if (!unreadMessages.isEmpty()) {
            for (Message m : unreadMessages) {
                m.setRead(true);
            }
            repository.saveAll(unreadMessages);
        }
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
