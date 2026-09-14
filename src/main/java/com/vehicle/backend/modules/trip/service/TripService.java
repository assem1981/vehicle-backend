package com.vehicle.backend.modules.trip.service;

import com.vehicle.backend.modules.trip.dto.TripDto;

import java.util.List;

public interface TripService {
    TripDto startTrip(Long bookingId, Integer startOdometer);
    TripDto endTrip(Long tripId, Integer endOdometer);
    TripDto getTripByBooking(Long bookingId);
    List<TripDto> getTripsByUser(Long userId);
    TripDto updateLocation(Long tripId, Double lat, Double lng);
}
