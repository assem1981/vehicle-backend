package com.vehicle.backend.modules.pricing.controller;

import com.vehicle.backend.modules.pricing.dto.PricingRuleDto;
import com.vehicle.backend.modules.pricing.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @PostMapping("/rules")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<PricingRuleDto> addRule(
            @RequestParam Long vehicleId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam BigDecimal multiplier,
            @RequestParam(required = false) String ruleName) {
        return ResponseEntity.ok(pricingService.addPricingRule(vehicleId, startDate, endDate, multiplier, ruleName));
    }

    @GetMapping("/rules/{vehicleId}")
    public ResponseEntity<List<PricingRuleDto>> getRules(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(pricingService.getVehiclePricingRules(vehicleId));
    }

    @GetMapping("/calculate")
    public ResponseEntity<BigDecimal> calculatePrice(
            @RequestParam Long vehicleId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(pricingService.calculatePrice(vehicleId, startDate, endDate));
    }
}
