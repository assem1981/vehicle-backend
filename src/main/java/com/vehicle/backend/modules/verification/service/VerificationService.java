package com.vehicle.backend.modules.verification.service;

import com.vehicle.backend.modules.verification.dto.VerificationDto;
import com.vehicle.backend.modules.verification.entity.VerificationStatus;

public interface VerificationService {
    VerificationDto submitVerification(Long userId, String driverLicenseNumber, String identityDocumentUrl);
    VerificationDto getVerificationByUser(Long userId);
    VerificationDto reviewVerification(Long id, VerificationStatus status, String rejectionReason);
}
