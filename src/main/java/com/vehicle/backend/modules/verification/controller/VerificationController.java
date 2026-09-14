package com.vehicle.backend.modules.verification.controller;

import com.vehicle.backend.modules.verification.dto.VerificationDto;
import com.vehicle.backend.modules.verification.entity.VerificationStatus;
import com.vehicle.backend.modules.verification.service.VerificationService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping
    public ResponseEntity<VerificationDto> submitVerification(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String driverLicenseNumber,
            @RequestParam String identityDocumentUrl) {
        return ResponseEntity.ok(verificationService.submitVerification(userPrincipal.getId(), driverLicenseNumber, identityDocumentUrl));
    }

    @GetMapping("/me")
    public ResponseEntity<VerificationDto> getMyVerification(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(verificationService.getVerificationByUser(userPrincipal.getId()));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VerificationDto> reviewVerification(
            @PathVariable Long id,
            @RequestParam VerificationStatus status,
            @RequestParam(required = false) String rejectionReason) {
        return ResponseEntity.ok(verificationService.reviewVerification(id, status, rejectionReason));
    }
}
