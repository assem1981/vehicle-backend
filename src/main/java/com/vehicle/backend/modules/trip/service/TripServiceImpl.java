package com.vehicle.backend.modules.trip.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.booking.entity.Booking;
import com.vehicle.backend.modules.booking.repository.BookingRepository;
import com.vehicle.backend.modules.trip.dto.TripDto;
import com.vehicle.backend.modules.trip.entity.Trip;
import com.vehicle.backend.modules.trip.entity.TripStatus;
import com.vehicle.backend.modules.trip.repository.TripRepository;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final JmsTemplate jmsTemplate;

    @Override
    @Transactional
    public TripDto startTrip(Long bookingId, Integer startOdometer) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        vehicle.setStatus(VehicleStatus.IN_USE);
        vehicleRepository.save(vehicle);

        Trip trip = Trip.builder()
                .bookingId(bookingId)
                .userId(booking.getUserId())
                .vehicleId(booking.getVehicleId())
                .startOdometer(startOdometer)
                .status(TripStatus.IN_PROGRESS)
                .startTime(LocalDateTime.now())
                .build();

        Trip saved = tripRepository.save(trip);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "TRIP_STARTED",
                "tripId", saved.getId(),
                "bookingId", bookingId
        ));

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public TripDto endTrip(Long tripId, Integer endOdometer) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));

        trip.setEndOdometer(endOdometer);
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());

        Vehicle vehicle = vehicleRepository.findById(trip.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        Trip saved = tripRepository.save(trip);

        jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                "title", "Trip Completed",
                "message", "Your trip #" + tripId + " has been completed. Thank you!"
        ));

        return mapToDto(saved);
    }

    @Override
    public TripDto getTripByBooking(Long bookingId) {
        Trip trip = tripRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found for booking ID: " + bookingId));
        return mapToDto(trip);
    }

    @Override
    public List<TripDto> getTripsByUser(Long userId) {
        return tripRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TripDto updateLocation(Long tripId, Double lat, Double lng) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
        trip.setCurrentLatitude(lat);
        trip.setCurrentLongitude(lng);
        return mapToDto(tripRepository.save(trip));
    }

    private TripDto mapToDto(Trip trip) {
        return TripDto.builder()
                .id(trip.getId())
                .bookingId(trip.getBookingId())
                .userId(trip.getUserId())
                .vehicleId(trip.getVehicleId())
                .startOdometer(trip.getStartOdometer())
                .endOdometer(trip.getEndOdometer())
                .currentLatitude(trip.getCurrentLatitude())
                .currentLongitude(trip.getCurrentLongitude())
                .status(trip.getStatus())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .build();
    }
}
