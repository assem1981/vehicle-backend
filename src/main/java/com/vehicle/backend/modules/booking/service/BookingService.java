package com.vehicle.backend.modules.booking.service;

import com.vehicle.backend.modules.booking.dto.BookingDto;
import com.vehicle.backend.modules.booking.dto.BookingRequestDto;
import com.vehicle.backend.modules.booking.entity.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingRequestDto request);
    BookingDto getBookingById(Long id);
    List<BookingDto> getBookingsByUser(Long userId);
    List<BookingDto> getBookingsByVehicle(Long vehicleId);
    List<BookingDto> getBookingsByHost(Long hostUserId);
    List<BookingDto> getAllBookings();
    BookingDto updateBookingStatus(Long id, BookingStatus status);
}
