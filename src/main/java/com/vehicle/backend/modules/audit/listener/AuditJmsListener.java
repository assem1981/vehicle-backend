package com.vehicle.backend.modules.audit.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditJmsListener {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = ActiveMQConfig.AUDIT_QUEUE)
    public void handleAuditEvent(Map<String, Object> payload) {
        log.info("Received JMS audit event: {}", payload);
        try {
            String action = (String) payload.getOrDefault("action", "UNKNOWN_ACTION");
            String details = objectMapper.writeValueAsString(payload);
            auditLogService.recordAuditLog(action, details);
        } catch (Exception ex) {
            log.error("Failed to persist audit log from JMS message", ex);
        }
    }
}
