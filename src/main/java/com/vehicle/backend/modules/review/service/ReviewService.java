package com.vehicle.backend.modules.review.service;

import com.vehicle.backend.modules.review.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto submitReview(Long userId, Long bookingId, Long vehicleId, Integer rating, String comment);
    List<ReviewDto> getVehicleReviews(Long vehicleId);
    Double getAverageRatingForVehicle(Long vehicleId);
}
