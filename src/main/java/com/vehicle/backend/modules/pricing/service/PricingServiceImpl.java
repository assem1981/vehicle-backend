package com.vehicle.backend.modules.pricing.service;

import com.vehicle.backend.modules.pricing.dto.PricingRuleDto;
import com.vehicle.backend.modules.pricing.entity.PricingRule;
import com.vehicle.backend.modules.pricing.repository.PricingRepository;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PricingRepository pricingRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @CacheEvict(value = "calculated_prices", allEntries = true)
    public PricingRuleDto addPricingRule(Long vehicleId, LocalDate startDate, LocalDate endDate, BigDecimal multiplier, String ruleName) {
        PricingRule rule = PricingRule.builder()
                .vehicleId(vehicleId)
                .startDate(startDate)
                .endDate(endDate)
                .multiplier(multiplier)
                .ruleName(ruleName)
                .build();

        PricingRule saved = pricingRepository.save(rule);
        return mapToDto(saved);
    }

    @Override
    public List<PricingRuleDto> getVehiclePricingRules(Long vehicleId) {
        return pricingRepository.findByVehicleId(vehicleId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "calculated_prices", key = "#vehicleId + '-' + #startDate + '-' + #endDate")
    public BigDecimal calculatePrice(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) days = 1;

        BigDecimal baseDailyRate = vehicle.getDailyRate();
        List<PricingRule> rules = pricingRepository.findByVehicleId(vehicleId);

        BigDecimal effectiveMultiplier = BigDecimal.ONE;
        for (PricingRule rule : rules) {
            if (rule.getStartDate() != null && rule.getEndDate() != null) {
                if (!startDate.isBefore(rule.getStartDate()) && !endDate.isAfter(rule.getEndDate())) {
                    effectiveMultiplier = effectiveMultiplier.multiply(rule.getMultiplier());
                }
            }
        }

        return baseDailyRate.multiply(BigDecimal.valueOf(days)).multiply(effectiveMultiplier);
    }

    private PricingRuleDto mapToDto(PricingRule rule) {
        return PricingRuleDto.builder()
                .id(rule.getId())
                .vehicleId(rule.getVehicleId())
                .startDate(rule.getStartDate())
                .endDate(rule.getEndDate())
                .multiplier(rule.getMultiplier())
                .ruleName(rule.getRuleName())
                .build();
    }
}
