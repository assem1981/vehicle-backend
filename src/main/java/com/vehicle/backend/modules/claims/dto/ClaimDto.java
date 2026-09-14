package com.vehicle.backend.modules.claims.dto;

import com.vehicle.backend.modules.claims.entity.ClaimStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ClaimDto {
    private Long id;
    private Long bookingId;
    private Long reporterUserId;
    private String description;
    private BigDecimal estimatedAmount;
    private String evidenceImageUrls;
    private ClaimStatus status;
    private LocalDateTime createdAt;
}
