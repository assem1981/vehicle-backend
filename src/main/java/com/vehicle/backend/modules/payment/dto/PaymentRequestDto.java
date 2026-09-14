package com.vehicle.backend.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull
    private Long bookingId;

    @NotNull
    private BigDecimal amount;

    private String paymentMethod;
}
