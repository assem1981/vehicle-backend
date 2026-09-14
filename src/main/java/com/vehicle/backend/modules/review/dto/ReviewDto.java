package com.vehicle.backend.modules.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {
    private Long id;
    private Long bookingId;
    private Long authorUserId;
    private Long targetVehicleId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
