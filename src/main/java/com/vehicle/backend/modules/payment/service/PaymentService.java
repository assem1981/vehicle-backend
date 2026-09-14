package com.vehicle.backend.modules.payment.service;

import com.vehicle.backend.modules.payment.dto.PaymentDto;
import com.vehicle.backend.modules.payment.dto.PaymentRequestDto;

import java.util.List;

public interface PaymentService {
    PaymentDto processPayment(Long userId, PaymentRequestDto request);
    PaymentDto getPaymentByBooking(Long bookingId);
    List<PaymentDto> getPaymentsByUser(Long userId);
    PaymentDto refundPayment(Long paymentId);
}
