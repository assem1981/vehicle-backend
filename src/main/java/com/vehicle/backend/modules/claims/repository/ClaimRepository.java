package com.vehicle.backend.modules.claims.repository;

import com.vehicle.backend.modules.claims.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByBookingId(Long bookingId);
    List<Claim> findByReporterUserId(Long reporterUserId);
}
