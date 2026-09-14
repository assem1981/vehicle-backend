package com.vehicle.backend.modules.review.repository;

import com.vehicle.backend.modules.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTargetVehicleId(Long targetVehicleId);
    List<Review> findByAuthorUserId(Long authorUserId);
}
