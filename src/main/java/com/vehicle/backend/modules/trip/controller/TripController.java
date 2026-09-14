package com.vehicle.backend.modules.trip.controller;

import com.vehicle.backend.modules.trip.dto.TripDto;
import com.vehicle.backend.modules.trip.service.TripService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping("/start")
    public ResponseEntity<TripDto> startTrip(
            @RequestParam Long bookingId,
            @RequestParam Integer startOdometer) {
        return ResponseEntity.ok(tripService.startTrip(bookingId, startOdometer));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<TripDto> endTrip(
            @PathVariable Long id,
            @RequestParam Integer endOdometer) {
        return ResponseEntity.ok(tripService.endTrip(id, endOdometer));
    }

    @GetMapping("/me")
    public ResponseEntity<List<TripDto>> getMyTrips(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(tripService.getTripsByUser(userPrincipal.getId()));
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<TripDto> updateLocation(
            @PathVariable Long id,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        return ResponseEntity.ok(tripService.updateLocation(id, lat, lng));
    }
}
