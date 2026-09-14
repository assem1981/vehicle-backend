package com.vehicle.backend.modules.promotions.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PromotionDto {
    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private LocalDate expirationDate;
    private Integer usageLimit;
    private Integer timesUsed;
    private boolean active;
}
