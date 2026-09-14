package com.vehicle.backend.modules.promotions.controller;

import com.vehicle.backend.modules.promotions.dto.PromotionDto;
import com.vehicle.backend.modules.promotions.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionDto> createPromotion(
            @RequestParam String code,
            @RequestParam BigDecimal discountPercentage,
            @RequestParam(required = false) LocalDate expirationDate,
            @RequestParam(required = false) Integer usageLimit) {
        return ResponseEntity.ok(promotionService.createPromotion(code, discountPercentage, expirationDate, usageLimit));
    }

    @PostMapping("/validate")
    public ResponseEntity<PromotionDto> validatePromo(@RequestParam String code) {
        return ResponseEntity.ok(promotionService.validateAndApplyPromo(code));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PromotionDto>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }
}
