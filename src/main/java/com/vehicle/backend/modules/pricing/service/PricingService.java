package com.vehicle.backend.modules.pricing.service;

import com.vehicle.backend.modules.pricing.dto.PricingRuleDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PricingService {
    PricingRuleDto addPricingRule(Long vehicleId, LocalDate startDate, LocalDate endDate, BigDecimal multiplier, String ruleName);
    List<PricingRuleDto> getVehiclePricingRules(Long vehicleId);
    BigDecimal calculatePrice(Long vehicleId, LocalDate startDate, LocalDate endDate);
}
