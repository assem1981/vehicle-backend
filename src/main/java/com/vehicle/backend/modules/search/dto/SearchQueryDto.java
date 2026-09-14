package com.vehicle.backend.modules.search.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SearchQueryDto {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal maxPrice;
    private String category;
}
