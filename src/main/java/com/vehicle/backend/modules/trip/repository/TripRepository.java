package com.vehicle.backend.modules.trip.repository;

import com.vehicle.backend.modules.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByBookingId(Long bookingId);
    List<Trip> findByUserId(Long userId);
    List<Trip> findByVehicleId(Long vehicleId);
}
