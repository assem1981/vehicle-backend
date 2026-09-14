package com.vehicle.backend.modules.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDto {
    private long totalUsers;
    private long totalHosts;
    private long totalVehicles;
    private long totalBookings;
    private long pendingApprovalsCount;
    private BigDecimal totalRevenue;
}
