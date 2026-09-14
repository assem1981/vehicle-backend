package com.vehicle.backend.modules.admin.dto;

import com.vehicle.backend.modules.user.entity.HostApprovalStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HostApplicationResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private HostApprovalStatus hostApprovalStatus;
    private LocalDateTime createdAt;
}
