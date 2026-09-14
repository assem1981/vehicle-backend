package com.vehicle.backend.modules.payment.dto;

import com.vehicle.backend.modules.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionReference;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
