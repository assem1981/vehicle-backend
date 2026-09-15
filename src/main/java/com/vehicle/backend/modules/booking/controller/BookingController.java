package com.vehicle.backend.modules.booking.controller;

import com.vehicle.backend.modules.booking.dto.BookingDto;
import com.vehicle.backend.modules.booking.dto.BookingRequestDto;
import com.vehicle.backend.modules.booking.entity.BookingStatus;
import com.vehicle.backend.modules.booking.service.BookingService;
import com.vehicle.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody BookingRequestDto request) {
        if (userPrincipal == null || userPrincipal.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(bookingService.createBooking(userPrincipal.getId(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingDto>> getMyBookings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<BookingDto>> getBookingsByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(bookingService.getBookingsByVehicle(vehicleId));
    }

    @GetMapping("/host/me")
    public ResponseEntity<List<BookingDto>> getMyHostBookings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(bookingService.getBookingsByHost(userId));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<BookingDto>> getBookingsByHostId(@PathVariable Long hostId) {
        return ResponseEntity.ok(bookingService.getBookingsByHost(hostId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<BookingDto>> getAllBookingsForAdmin() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }
}
