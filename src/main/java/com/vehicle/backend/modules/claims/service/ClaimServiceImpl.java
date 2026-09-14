package com.vehicle.backend.modules.claims.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.claims.dto.ClaimDto;
import com.vehicle.backend.modules.claims.entity.Claim;
import com.vehicle.backend.modules.claims.entity.ClaimStatus;
import com.vehicle.backend.modules.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository repository;
    private final JmsTemplate jmsTemplate;

    @Override
    public ClaimDto fileClaim(Long reporterId, Long bookingId, String description, BigDecimal estimatedAmount, String evidenceImageUrls) {
        Claim claim = Claim.builder()
                .bookingId(bookingId)
                .reporterUserId(reporterId)
                .description(description)
                .estimatedAmount(estimatedAmount)
                .evidenceImageUrls(evidenceImageUrls)
                .status(ClaimStatus.OPEN)
                .build();

        Claim saved = repository.save(claim);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "FILE_CLAIM",
                "claimId", saved.getId(),
                "bookingId", bookingId,
                "amount", estimatedAmount
        ));

        return mapToDto(saved);
    }

    @Override
    public ClaimDto getClaimById(Long id) {
        Claim claim = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + id));
        return mapToDto(claim);
    }

    @Override
    public List<ClaimDto> getClaimsByBooking(Long bookingId) {
        return repository.findByBookingId(bookingId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimDto> getClaimsByReporter(Long reporterId) {
        return repository.findByReporterUserId(reporterId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClaimDto updateClaimStatus(Long id, ClaimStatus status) {
        Claim claim = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + id));
        claim.setStatus(status);
        return mapToDto(repository.save(claim));
    }

    private ClaimDto mapToDto(Claim claim) {
        return ClaimDto.builder()
                .id(claim.getId())
                .bookingId(claim.getBookingId())
                .reporterUserId(claim.getReporterUserId())
                .description(claim.getDescription())
                .estimatedAmount(claim.getEstimatedAmount())
                .evidenceImageUrls(claim.getEvidenceImageUrls())
                .status(claim.getStatus())
                .createdAt(claim.getCreatedAt())
                .build();
    }
}
