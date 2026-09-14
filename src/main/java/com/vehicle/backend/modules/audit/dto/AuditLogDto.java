package com.vehicle.backend.modules.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogDto {
    private Long id;
    private String action;
    private String details;
    private LocalDateTime timestamp;
}
