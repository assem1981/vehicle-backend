package com.vehicle.backend.modules.vehicle.dto;

import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private Long id;
    private Long hostId;
    private String make;
    private String model;
    private Integer year;
    private String vin;
    private String category;
    private String transmission;
    private Integer seatingCapacity;
    private String fuelType;
    private BigDecimal dailyRate;
    private String locationCity;
    private Double latitude;
    private Double longitude;
    private VehicleStatus status;
    private String imageUrl;
    private String interiorImageUrl;
    private String hostName;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
}
