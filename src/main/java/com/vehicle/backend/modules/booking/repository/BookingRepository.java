package com.vehicle.backend.modules.booking.repository;

import com.vehicle.backend.modules.booking.entity.Booking;
import com.vehicle.backend.modules.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByVehicleId(Long vehicleId);

    @Query("SELECT b FROM Booking b WHERE b.vehicleId = :vehicleId AND b.status != 'CANCELLED' " +
           "AND (:startDate <= b.endDate AND :endDate >= b.startDate)")
    List<Booking> findOverlappingBookings(Long vehicleId, LocalDate startDate, LocalDate endDate);
}
