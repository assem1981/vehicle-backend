package com.vehicle.backend.modules.verification.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.modules.verification.dto.VerificationDto;
import com.vehicle.backend.modules.verification.entity.VerificationRecord;
import com.vehicle.backend.modules.verification.entity.VerificationStatus;
import com.vehicle.backend.modules.verification.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository repository;
    private final UserRepository userRepository;
    private final JmsTemplate jmsTemplate;

    @Override
    public VerificationDto submitVerification(Long userId, String driverLicenseNumber, String identityDocumentUrl) {
        VerificationRecord record = repository.findByUserId(userId)
                .orElse(VerificationRecord.builder().userId(userId).build());

        record.setDriverLicenseNumber(driverLicenseNumber);
        record.setIdentityDocumentUrl(identityDocumentUrl);
        record.setStatus(VerificationStatus.PENDING);

        VerificationRecord saved = repository.save(record);
        return mapToDto(saved);
    }

    @Override
    public VerificationDto getVerificationByUser(Long userId) {
        VerificationRecord record = repository.findByUserId(userId)
                .orElseGet(() -> VerificationRecord.builder().userId(userId).status(VerificationStatus.UNVERIFIED).build());
        return mapToDto(record);
    }

    @Override
    public VerificationDto reviewVerification(Long id, VerificationStatus status, String rejectionReason) {
        VerificationRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Verification record not found with ID: " + id));

        record.setStatus(status);
        if (rejectionReason != null) record.setRejectionReason(rejectionReason);

        VerificationRecord saved = repository.save(record);

        User user = userRepository.findById(saved.getUserId()).orElse(null);
        if (user != null) {
            jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                    "recipientEmail", user.getEmail(),
                    "title", "Identity Verification Update",
                    "message", "Your identity verification status is now: " + status
            ));
        }

        return mapToDto(saved);
    }

    private VerificationDto mapToDto(VerificationRecord record) {
        return VerificationDto.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .driverLicenseNumber(record.getDriverLicenseNumber())
                .identityDocumentUrl(record.getIdentityDocumentUrl())
                .status(record.getStatus())
                .rejectionReason(record.getRejectionReason())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
