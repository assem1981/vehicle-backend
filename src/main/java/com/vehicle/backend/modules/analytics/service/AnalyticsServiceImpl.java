package com.vehicle.backend.modules.analytics.service;

import com.vehicle.backend.modules.analytics.dto.AnalyticsSummaryDto;
import com.vehicle.backend.modules.booking.repository.BookingRepository;
import com.vehicle.backend.modules.host.repository.HostRepository;
import com.vehicle.backend.modules.payment.entity.Payment;
import com.vehicle.backend.modules.payment.entity.PaymentStatus;
import com.vehicle.backend.modules.payment.repository.PaymentRepository;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Cacheable(value = "analytics_summary")
    public AnalyticsSummaryDto getSystemAnalytics() {
        long usersCount = userRepository.count();
        long hostsCount = hostRepository.count();
        long vehiclesCount = vehicleRepository.count();
        long bookingsCount = bookingRepository.count();
        long pendingApprovals = vehicleRepository.countByStatus(VehicleStatus.PENDING_APPROVAL);

        BigDecimal revenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (revenue == null || revenue.compareTo(BigDecimal.ZERO) == 0) {
            revenue = bookingRepository.findAll().stream()
                    .filter(b -> b.getStatus() == com.vehicle.backend.modules.booking.entity.BookingStatus.CONFIRMED 
                              || b.getStatus() == com.vehicle.backend.modules.booking.entity.BookingStatus.IN_PROGRESS 
                              || b.getStatus() == com.vehicle.backend.modules.booking.entity.BookingStatus.ACTIVE 
                              || b.getStatus() == com.vehicle.backend.modules.booking.entity.BookingStatus.COMPLETED)
                    .map(com.vehicle.backend.modules.booking.entity.Booking::getTotalPrice)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return AnalyticsSummaryDto.builder()
                .totalUsers(usersCount)
                .totalHosts(hostsCount)
                .totalVehicles(vehiclesCount)
                .totalBookings(bookingsCount)
                .pendingApprovalsCount(pendingApprovals)
                .totalRevenue(revenue)
                .build();
    }
}
