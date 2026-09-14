package com.vehicle.backend.modules.trip.dto;

import com.vehicle.backend.modules.trip.entity.TripStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TripDto {
    private Long id;
    private Long bookingId;
    private Long userId;
    private Long vehicleId;
    private Integer startOdometer;
    private Integer endOdometer;
    private Double currentLatitude;
    private Double currentLongitude;
    private TripStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
