package com.vehicle.backend.modules.checkinout.controller;

import com.vehicle.backend.modules.checkinout.dto.CheckRecordDto;
import com.vehicle.backend.modules.checkinout.entity.CheckType;
import com.vehicle.backend.modules.checkinout.service.CheckInOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/check-in-out")
@RequiredArgsConstructor
public class CheckInOutController {

    private final CheckInOutService service;

    @PostMapping
    public ResponseEntity<CheckRecordDto> submitRecord(
            @RequestParam Long bookingId,
            @RequestParam CheckType type,
            @RequestParam Integer odometer,
            @RequestParam Integer fuelLevel,
            @RequestParam(required = false) String photoUrls,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(service.submitCheckRecord(bookingId, type, odometer, fuelLevel, photoUrls, notes));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<CheckRecordDto>> getRecordsForBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(service.getRecordsForBooking(bookingId));
    }
}
