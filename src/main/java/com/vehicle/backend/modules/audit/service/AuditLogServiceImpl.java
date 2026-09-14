package com.vehicle.backend.modules.audit.service;

import com.vehicle.backend.modules.audit.dto.AuditLogDto;
import com.vehicle.backend.modules.audit.entity.AuditLog;
import com.vehicle.backend.modules.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository repository;

    @Override
    public AuditLogDto recordAuditLog(String action, String details) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .details(details)
                .build();

        AuditLog saved = repository.save(auditLog);
        return mapToDto(saved);
    }

    @Override
    public List<AuditLogDto> getAllAuditLogs() {
        return repository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AuditLogDto mapToDto(AuditLog auditLog) {
        return AuditLogDto.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .details(auditLog.getDetails())
                .timestamp(auditLog.getTimestamp())
                .build();
    }
}
