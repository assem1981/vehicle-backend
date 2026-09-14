package com.vehicle.backend.modules.pricing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PricingRuleDto {
    private Long id;
    private Long vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal multiplier;
    private String ruleName;
}
