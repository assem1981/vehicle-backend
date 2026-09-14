package com.vehicle.backend.modules.review.controller;

import com.vehicle.backend.modules.review.dto.ReviewDto;
import com.vehicle.backend.modules.review.service.ReviewService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> submitReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long bookingId,
            @RequestParam Long vehicleId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(reviewService.submitReview(userPrincipal.getId(), bookingId, vehicleId, rating, comment));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ReviewDto>> getVehicleReviews(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reviewService.getVehicleReviews(vehicleId));
    }

    @GetMapping("/vehicle/{vehicleId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForVehicle(vehicleId));
    }
}
