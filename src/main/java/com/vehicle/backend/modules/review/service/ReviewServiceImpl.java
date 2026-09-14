package com.vehicle.backend.modules.review.service;

import com.vehicle.backend.modules.review.dto.ReviewDto;
import com.vehicle.backend.modules.review.entity.Review;
import com.vehicle.backend.modules.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;

    @Override
    @CacheEvict(value = "vehicles", key = "#vehicleId")
    public ReviewDto submitReview(Long userId, Long bookingId, Long vehicleId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars");
        }

        Review review = Review.builder()
                .authorUserId(userId)
                .bookingId(bookingId)
                .targetVehicleId(vehicleId)
                .rating(rating)
                .comment(comment)
                .build();

        Review saved = repository.save(review);
        return mapToDto(saved);
    }

    @Override
    public List<ReviewDto> getVehicleReviews(Long vehicleId) {
        return repository.findByTargetVehicleId(vehicleId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingForVehicle(Long vehicleId) {
        List<Review> reviews = repository.findByTargetVehicleId(vehicleId);
        if (reviews.isEmpty()) return 5.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
    }

    private ReviewDto mapToDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .bookingId(review.getBookingId())
                .authorUserId(review.getAuthorUserId())
                .targetVehicleId(review.getTargetVehicleId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
