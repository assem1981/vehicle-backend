package com.vehicle.backend.modules.checkinout.dto;

import com.vehicle.backend.modules.checkinout.entity.CheckType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CheckRecordDto {
    private Long id;
    private Long bookingId;
    private CheckType type;
    private Integer odometer;
    private Integer fuelLevelPercentage;
    private String lockboxCode;
    private String photoUrls;
    private String notes;
    private LocalDateTime timestamp;
}
