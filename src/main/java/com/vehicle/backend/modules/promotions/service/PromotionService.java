package com.vehicle.backend.modules.promotions.service;

import com.vehicle.backend.modules.promotions.dto.PromotionDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PromotionService {
    PromotionDto createPromotion(String code, BigDecimal discountPercentage, LocalDate expirationDate, Integer usageLimit);
    PromotionDto validateAndApplyPromo(String code);
    List<PromotionDto> getAllPromotions();
}
