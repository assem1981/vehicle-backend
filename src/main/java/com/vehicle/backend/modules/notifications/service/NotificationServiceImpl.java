package com.vehicle.backend.modules.notifications.service;

import com.vehicle.backend.modules.notifications.dto.NotificationDto;
import com.vehicle.backend.modules.notifications.entity.Notification;
import com.vehicle.backend.modules.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Override
    public NotificationDto createNotification(String recipientEmail, String title, String message) {
        Notification notification = Notification.builder()
                .recipientEmail(recipientEmail)
                .title(title)
                .message(message)
                .isRead(false)
                .build();

        Notification saved = repository.save(notification);
        return mapToDto(saved);
    }

    @Override
    public List<NotificationDto> getUserNotifications(String email) {
        return repository.findByRecipientEmailOrderByCreatedAtDesc(email).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {
        repository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .recipientEmail(notification.getRecipientEmail())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
