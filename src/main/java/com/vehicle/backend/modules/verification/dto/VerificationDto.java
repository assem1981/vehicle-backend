package com.vehicle.backend.modules.verification.dto;

import com.vehicle.backend.modules.verification.entity.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerificationDto {
    private Long id;
    private Long userId;
    private String driverLicenseNumber;
    private String identityDocumentUrl;
    private VerificationStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
