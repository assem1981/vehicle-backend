package com.vehicle.backend.modules.booking.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.booking.dto.BookingDto;
import com.vehicle.backend.modules.booking.dto.BookingRequestDto;
import com.vehicle.backend.modules.booking.entity.Booking;
import com.vehicle.backend.modules.booking.entity.BookingStatus;
import com.vehicle.backend.modules.booking.repository.BookingRepository;
import com.vehicle.backend.modules.host.repository.HostRepository;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final HostRepository hostRepository;
    private final JmsTemplate jmsTemplate;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingRequestDto request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + request.getVehicleId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Check availability overlap
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                request.getVehicleId(), request.getStartDate(), request.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new IllegalStateException("Vehicle is already booked for the selected dates");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days == 0) days = 1;

        BigDecimal totalPrice = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));

        Booking booking = Booking.builder()
                .vehicleId(vehicle.getId())
                .userId(userId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalPrice(totalPrice)
                .status(BookingStatus.CONFIRMED)
                .promoCode(request.getPromoCode())
                .build();

        Booking saved = bookingRepository.save(booking);

        // Publish ActiveMQ notifications & audit events (non-blocking if JMS service is offline)
        try {
            jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                    "title", "Booking Confirmed",
                    "message", "Your booking #" + saved.getId() + " for " + vehicle.getMake() + " " + vehicle.getModel() + " is confirmed!"
            ));

            jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                    "action", "CREATE_BOOKING",
                    "bookingId", saved.getId(),
                    "userId", userId,
                    "totalPrice", totalPrice
            ));
        } catch (Exception e) {
            System.err.println("ActiveMQ notification notice (non-blocking): " + e.getMessage());
        }

        return mapToDto(saved);
    }

    @Override
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
        return mapToDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByVehicle(Long vehicleId) {
        return bookingRepository.findByVehicleId(vehicleId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByHost(Long hostUserId) {
        com.vehicle.backend.modules.host.entity.HostProfile profile = hostRepository.findByUserId(hostUserId).orElse(null);

        java.util.Set<Long> hostIds = new java.util.HashSet<>();
        if (hostUserId != null) hostIds.add(hostUserId);
        if (profile != null && profile.getId() != null) hostIds.add(profile.getId());

        List<Long> vehicleIds = vehicleRepository.findAll().stream()
                .filter(v -> v.getHostId() != null && hostIds.contains(v.getHostId()))
                .map(Vehicle::getId)
                .collect(Collectors.toList());

        if (vehicleIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return bookingRepository.findAll().stream()
                .filter(b -> b.getVehicleId() != null && vehicleIds.contains(b.getVehicleId()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
        booking.setStatus(status);
        return mapToDto(bookingRepository.save(booking));
    }

    private BookingDto mapToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .vehicleId(booking.getVehicleId())
                .userId(booking.getUserId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .promoCode(booking.getPromoCode())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
