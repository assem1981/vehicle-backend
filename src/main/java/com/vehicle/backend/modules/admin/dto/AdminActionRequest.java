package com.vehicle.backend.modules.admin.dto;

import lombok.Data;

@Data
public class AdminActionRequest {
    private Long targetUserId;
    private Boolean active;
    private String reason;
}
