package com.vehicle.backend.modules.claims.controller;

import com.vehicle.backend.modules.claims.dto.ClaimDto;
import com.vehicle.backend.modules.claims.entity.ClaimStatus;
import com.vehicle.backend.modules.claims.service.ClaimService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    public ResponseEntity<ClaimDto> fileClaim(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long bookingId,
            @RequestParam String description,
            @RequestParam BigDecimal estimatedAmount,
            @RequestParam(required = false) String evidenceImageUrls) {
        return ResponseEntity.ok(claimService.fileClaim(userPrincipal.getId(), bookingId, description, estimatedAmount, evidenceImageUrls));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDto> getClaimById(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.getClaimById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ClaimDto>> getMyClaims(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(claimService.getClaimsByReporter(userPrincipal.getId()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimDto> updateClaimStatus(
            @PathVariable Long id,
            @RequestParam ClaimStatus status) {
        return ResponseEntity.ok(claimService.updateClaimStatus(id, status));
    }
}
