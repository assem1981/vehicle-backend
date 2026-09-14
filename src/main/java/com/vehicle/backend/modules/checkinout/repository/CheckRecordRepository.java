package com.vehicle.backend.modules.checkinout.repository;

import com.vehicle.backend.modules.checkinout.entity.CheckRecord;
import com.vehicle.backend.modules.checkinout.entity.CheckType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckRecordRepository extends JpaRepository<CheckRecord, Long> {
    List<CheckRecord> findByBookingId(Long bookingId);
    List<CheckRecord> findByBookingIdAndType(Long bookingId, CheckType type);
}
