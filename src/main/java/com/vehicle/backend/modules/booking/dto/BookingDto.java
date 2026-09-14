package com.vehicle.backend.modules.booking.dto;

import com.vehicle.backend.modules.booking.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private Long vehicleId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private String promoCode;
    private LocalDateTime createdAt;
}
