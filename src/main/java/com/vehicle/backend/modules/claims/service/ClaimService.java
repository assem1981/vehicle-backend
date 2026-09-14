package com.vehicle.backend.modules.claims.service;

import com.vehicle.backend.modules.claims.dto.ClaimDto;
import com.vehicle.backend.modules.claims.entity.ClaimStatus;

import java.math.BigDecimal;
import java.util.List;

public interface ClaimService {
    ClaimDto fileClaim(Long reporterId, Long bookingId, String description, BigDecimal estimatedAmount, String evidenceImageUrls);
    ClaimDto getClaimById(Long id);
    List<ClaimDto> getClaimsByBooking(Long bookingId);
    List<ClaimDto> getClaimsByReporter(Long reporterId);
    ClaimDto updateClaimStatus(Long id, ClaimStatus status);
}
