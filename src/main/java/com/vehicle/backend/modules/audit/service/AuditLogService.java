package com.vehicle.backend.modules.audit.service;

import com.vehicle.backend.modules.audit.dto.AuditLogDto;

import java.util.List;

public interface AuditLogService {
    AuditLogDto recordAuditLog(String action, String details);
    List<AuditLogDto> getAllAuditLogs();
}
