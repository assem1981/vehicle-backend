package com.vehicle.backend.modules.notifications.service;

import com.vehicle.backend.modules.notifications.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(String recipientEmail, String title, String message);
    List<NotificationDto> getUserNotifications(String email);
    void markAsRead(Long notificationId);
}
