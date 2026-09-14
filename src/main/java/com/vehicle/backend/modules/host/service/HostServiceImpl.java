package com.vehicle.backend.modules.host.service;

import com.vehicle.backend.modules.booking.entity.Booking;
import com.vehicle.backend.modules.booking.entity.BookingStatus;
import com.vehicle.backend.modules.booking.repository.BookingRepository;
import com.vehicle.backend.modules.host.dto.HostDto;
import com.vehicle.backend.modules.host.entity.HostProfile;
import com.vehicle.backend.modules.host.repository.HostRepository;
import com.vehicle.backend.modules.payment.entity.Payment;
import com.vehicle.backend.modules.payment.repository.PaymentRepository;
import com.vehicle.backend.modules.user.entity.Role;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostServiceImpl implements HostService {

    private final HostRepository hostRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public HostDto registerHost(Long userId, String bio, String payoutAccountDetails) {
        if (hostRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Host profile already exists for user ID: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.getRoles().add(Role.ROLE_HOST);
        userRepository.save(user);

        HostProfile profile = HostProfile.builder()
                .userId(userId)
                .bio(bio)
                .payoutAccountDetails(payoutAccountDetails)
                .averageRating(5.0)
                .totalTripsCount(0)
                .totalEarnings(BigDecimal.ZERO)
                .superHost(false)
                .build();

        HostProfile saved = hostRepository.save(profile);
        return mapToDto(saved);
    }

    @Override
    public HostDto getHostByUserId(Long userId) {
        HostProfile profile = hostRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Host profile not found for user ID: " + userId));
        return mapToDto(profile);
    }

    @Override
    public HostDto getHostById(Long id) {
        HostProfile profile = hostRepository.findById(id)
                .orElseGet(() -> hostRepository.findByUserId(id).orElse(null));

        if (profile != null) {
            return mapToDto(profile);
        }

        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return HostDto.builder()
                    .id(user.getId())
                    .userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .bio("Fleet Host")
                    .averageRating(5.0)
                    .totalTripsCount(0)
                    .totalEarnings(BigDecimal.ZERO)
                    .superHost(false)
                    .build();
        }

        throw new IllegalArgumentException("Host profile not found with ID or User ID: " + id);
    }

    @Override
    public List<HostDto> getAllHosts() {
        return hostRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public HostDto updatePayoutDetails(Long userId, String payoutDetails) {
        HostProfile profile = hostRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Host profile not found for user ID: " + userId));
        profile.setPayoutAccountDetails(payoutDetails);
        return mapToDto(hostRepository.save(profile));
    }

    @Override
    public Map<String, Object> getEarningsSummary(Long hostUserId) {
        HostProfile profile = hostRepository.findByUserId(hostUserId).orElse(null);

        Set<Long> hostIds = new HashSet<>();
        if (hostUserId != null) hostIds.add(hostUserId);
        if (profile != null && profile.getId() != null) hostIds.add(profile.getId());

        List<Vehicle> hostVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getHostId() != null && hostIds.contains(v.getHostId()))
                .collect(Collectors.toList());

        List<Long> vehicleIds = hostVehicles.stream().map(Vehicle::getId).collect(Collectors.toList());

        List<Booking> hostBookings;
        if (!vehicleIds.isEmpty()) {
            hostBookings = bookingRepository.findAll().stream()
                    .filter(b -> b.getVehicleId() != null && vehicleIds.contains(b.getVehicleId()))
                    .collect(Collectors.toList());
        } else {
            hostBookings = Collections.emptyList();
        }

        List<Booking> validBookings = hostBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED 
                          || b.getStatus() == BookingStatus.IN_PROGRESS
                          || b.getStatus() == BookingStatus.ACTIVE 
                          || b.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());

        int completedBookingsCount = (int) hostBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .count();

        BigDecimal totalEarningsBD = validBookings.stream()
                .map(Booking::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double totalEarnings = totalEarningsBD.doubleValue();
        if (totalEarnings == 0.0 && profile != null && profile.getTotalEarnings() != null && profile.getTotalEarnings().doubleValue() > 0) {
            totalEarnings = profile.getTotalEarnings().doubleValue();
        }

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        double monthlyEarnings = validBookings.stream()
                .filter(b -> {
                    LocalDate startDate = b.getStartDate();
                    LocalDate endDate = b.getEndDate();
                    if (startDate == null && b.getCreatedAt() != null) {
                        startDate = b.getCreatedAt().toLocalDate();
                    }
                    if (startDate == null) return false;
                    if (endDate == null) endDate = startDate;

                    // Booking overlaps current calendar month if startDate <= endOfMonth AND endDate >= startOfMonth
                    return !startDate.isAfter(endOfMonth) && !endDate.isBefore(startOfMonth);
                })
                .map(Booking::getTotalPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        double pendingPayout = validBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED 
                          || b.getStatus() == BookingStatus.IN_PROGRESS
                          || b.getStatus() == BookingStatus.ACTIVE)
                .map(Booking::getTotalPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        List<Map<String, Object>> payoutHistory = validBookings.stream()
                .map(b -> {
                    Payment payment = paymentRepository.findByBookingId(b.getId()).orElse(null);
                    String status = (payment != null && payment.getStatus() != null) ? payment.getStatus().name() : "PAID";
                    String dateStr = b.getCreatedAt() != null ? b.getCreatedAt().toLocalDate().toString() : now.toString();
                    return Map.<String, Object>of(
                        "id", "PAY-" + b.getId(),
                        "date", dateStr,
                        "amountUSD", b.getTotalPrice() != null ? b.getTotalPrice().doubleValue() : 0.0,
                        "status", status,
                        "bankAccount", "•••• 4829"
                    );
                })
                .collect(Collectors.toList());

        return Map.of(
            "totalEarningsUSD", Math.round(totalEarnings * 100.0) / 100.0,
            "monthlyEarningsUSD", Math.round(monthlyEarnings * 100.0) / 100.0,
            "pendingPayoutUSD", Math.round(pendingPayout * 100.0) / 100.0,
            "completedBookingsCount", completedBookingsCount,
            "payoutHistory", payoutHistory
        );
    }

    private HostDto mapToDto(HostProfile profile) {
        String firstName = null;
        String lastName = null;
        if (profile.getUserId() != null) {
            User u = userRepository.findById(profile.getUserId()).orElse(null);
            if (u != null) {
                firstName = u.getFirstName();
                lastName = u.getLastName();
            }
        }

        return HostDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(firstName)
                .lastName(lastName)
                .bio(profile.getBio())
                .payoutAccountDetails(profile.getPayoutAccountDetails())
                .averageRating(profile.getAverageRating())
                .totalTripsCount(profile.getTotalTripsCount())
                .totalEarnings(profile.getTotalEarnings())
                .superHost(profile.isSuperHost())
                .build();
    }
}
