package com.vehicle.backend.modules.checkinout.service;

import com.vehicle.backend.modules.checkinout.dto.CheckRecordDto;
import com.vehicle.backend.modules.checkinout.entity.CheckType;

import java.util.List;

public interface CheckInOutService {
    CheckRecordDto submitCheckRecord(Long bookingId, CheckType type, Integer odometer, Integer fuelLevel, String photoUrls, String notes);
    List<CheckRecordDto> getRecordsForBooking(Long bookingId);
}
