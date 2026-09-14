package com.vehicle.backend.modules.notifications.listener;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationJmsListener {

    private final NotificationService notificationService;

    @JmsListener(destination = ActiveMQConfig.NOTIFICATION_QUEUE)
    public void handleNotificationMessage(Map<String, Object> payload) {
        log.info("Received JMS notification payload: {}", payload);
        String recipient = (String) payload.getOrDefault("recipientEmail", "user@example.com");
        String title = (String) payload.getOrDefault("title", "System Notification");
        String message = (String) payload.getOrDefault("message", "You have a new message.");

        notificationService.createNotification(recipient, title, message);
    }
}
