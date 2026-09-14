package com.vehicle.backend.modules.payment.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.payment.dto.PaymentDto;
import com.vehicle.backend.modules.payment.dto.PaymentRequestDto;
import com.vehicle.backend.modules.payment.entity.Payment;
import com.vehicle.backend.modules.payment.entity.PaymentStatus;
import com.vehicle.backend.modules.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final JmsTemplate jmsTemplate;

    @Override
    @Transactional
    public PaymentDto processPayment(Long userId, PaymentRequestDto request) {
        String txRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(userId)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CREDIT_CARD")
                .transactionReference(txRef)
                .status(PaymentStatus.PAID)
                .build();

        Payment saved = paymentRepository.save(payment);

        try {
            jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                    "action", "PAYMENT_SUCCESS",
                    "paymentId", saved.getId(),
                    "bookingId", request.getBookingId(),
                    "amount", request.getAmount(),
                    "ref", txRef
            ));
        } catch (Exception e) {
            System.err.println("JMS payment audit notice (non-blocking): " + e.getMessage());
        }

        return mapToDto(saved);
    }

    @Override
    public PaymentDto getPaymentByBooking(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for booking ID: " + bookingId));
        return mapToDto(payment);
    }

    @Override
    public List<PaymentDto> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDto refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));
        payment.setStatus(PaymentStatus.REFUNDED);

        Payment saved = paymentRepository.save(payment);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "PAYMENT_REFUND",
                "paymentId", paymentId,
                "amount", payment.getAmount()
        ));

        return mapToDto(saved);
    }

    private PaymentDto mapToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(payment.getTransactionReference())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
