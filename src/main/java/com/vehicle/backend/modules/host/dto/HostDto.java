package com.vehicle.backend.modules.host.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class HostDto {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String payoutAccountDetails;
    private Double averageRating;
    private Integer totalTripsCount;
    private BigDecimal totalEarnings;
    private boolean superHost;
}
