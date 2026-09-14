package com.vehicle.backend.modules.promotions.service;

import com.vehicle.backend.modules.promotions.dto.PromotionDto;
import com.vehicle.backend.modules.promotions.entity.Promotion;
import com.vehicle.backend.modules.promotions.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository repository;

    @Override
    public PromotionDto createPromotion(String code, BigDecimal discountPercentage, LocalDate expirationDate, Integer usageLimit) {
        Promotion promotion = Promotion.builder()
                .code(code.toUpperCase())
                .discountPercentage(discountPercentage)
                .expirationDate(expirationDate)
                .usageLimit(usageLimit)
                .timesUsed(0)
                .active(true)
                .build();

        Promotion saved = repository.save(promotion);
        return mapToDto(saved);
    }

    @Override
    public PromotionDto validateAndApplyPromo(String code) {
        Promotion promo = repository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid promo code: " + code));

        if (!promo.isActive()) {
            throw new IllegalStateException("Promo code is inactive");
        }

        if (promo.getExpirationDate() != null && LocalDate.now().isAfter(promo.getExpirationDate())) {
            throw new IllegalStateException("Promo code has expired");
        }

        if (promo.getUsageLimit() != null && promo.getTimesUsed() >= promo.getUsageLimit()) {
            throw new IllegalStateException("Promo code usage limit reached");
        }

        promo.setTimesUsed(promo.getTimesUsed() + 1);
        return mapToDto(repository.save(promo));
    }

    @Override
    public List<PromotionDto> getAllPromotions() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PromotionDto mapToDto(Promotion promo) {
        return PromotionDto.builder()
                .id(promo.getId())
                .code(promo.getCode())
                .discountPercentage(promo.getDiscountPercentage())
                .expirationDate(promo.getExpirationDate())
                .usageLimit(promo.getUsageLimit())
                .timesUsed(promo.getTimesUsed())
                .active(promo.isActive())
                .build();
    }
}
