package com.vehicle.backend.modules.vehicle.service;

import com.vehicle.backend.modules.host.repository.HostRepository;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.modules.vehicle.dto.VehicleDto;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final HostRepository hostRepository;

    @Override
    @CacheEvict(value = {"vehicles", "search_vehicles", "analytics_summary"}, allEntries = true)
    public VehicleDto createVehicle(Long hostId, VehicleDto dto) {
        String creatorName = userRepository.findById(hostId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Unknown");

        Vehicle vehicle = Vehicle.builder()
                .hostId(hostId)
                .make(dto.getMake())
                .model(dto.getModel())
                .year(dto.getYear())
                .vin(dto.getVin())
                .category(dto.getCategory())
                .transmission(dto.getTransmission())
                .seatingCapacity(dto.getSeatingCapacity())
                .fuelType(dto.getFuelType())
                .dailyRate(dto.getDailyRate())
                .locationCity(dto.getLocationCity())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .status(VehicleStatus.PENDING_APPROVAL)
                .imageUrl(dto.getImageUrl())
                .interiorImageUrl(dto.getInteriorImageUrl())
                .createdBy(creatorName)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToDto(saved);
    }

    @Override
    @Cacheable(value = "vehicles", key = "#id")
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));
        return mapToDto(vehicle);
    }

    @Override
    public List<VehicleDto> getVehiclesByHost(Long hostId) {
        com.vehicle.backend.modules.host.entity.HostProfile profile = hostRepository.findByUserId(hostId).orElse(null);

        Set<Long> hostIds = new HashSet<>();
        if (hostId != null) hostIds.add(hostId);
        if (profile != null && profile.getId() != null) hostIds.add(profile.getId());

        return vehicleRepository.findAll().stream()
                .filter(v -> v.getHostId() != null && hostIds.contains(v.getHostId()))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> getAllAvailableVehicles() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> getAllVehiclesForAdmin() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDto> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"vehicles", "search_vehicles", "analytics_summary"}, allEntries = true)
    public VehicleDto updateVehicleStatus(Long id, VehicleStatus status, Long updatedByUserId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        String updaterName = userRepository.findById(updatedByUserId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Unknown");

        vehicle.setStatus(status);
        vehicle.setUpdatedBy(updaterName);
        return mapToDto(vehicleRepository.save(vehicle));
    }

    @Override
    @CacheEvict(value = {"vehicles", "search_vehicles", "analytics_summary"}, allEntries = true)
    public VehicleDto updateVehicle(Long id, VehicleDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + id));

        if (dto.getMake() != null) vehicle.setMake(dto.getMake());
        if (dto.getModel() != null) vehicle.setModel(dto.getModel());
        if (dto.getYear() != null) vehicle.setYear(dto.getYear());
        if (dto.getDailyRate() != null) vehicle.setDailyRate(dto.getDailyRate());
        if (dto.getLocationCity() != null) vehicle.setLocationCity(dto.getLocationCity());

        return mapToDto(vehicleRepository.save(vehicle));
    }

    @Override
    @CacheEvict(value = {"vehicles", "search_vehicles", "analytics_summary"}, allEntries = true)
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    private VehicleDto mapToDto(Vehicle vehicle) {
        String hostName = userRepository.findById(vehicle.getHostId())
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Unknown Host");

        return VehicleDto.builder()
                .id(vehicle.getId())
                .hostId(vehicle.getHostId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .vin(vehicle.getVin())
                .category(vehicle.getCategory())
                .transmission(vehicle.getTransmission())
                .seatingCapacity(vehicle.getSeatingCapacity())
                .fuelType(vehicle.getFuelType())
                .dailyRate(vehicle.getDailyRate())
                .locationCity(vehicle.getLocationCity())
                .latitude(vehicle.getLatitude())
                .longitude(vehicle.getLongitude())
                .status(vehicle.getStatus())
                .imageUrl(vehicle.getImageUrl())
                .interiorImageUrl(vehicle.getInteriorImageUrl())
                .hostName(hostName)
                .createdBy(vehicle.getCreatedBy())
                .updatedBy(vehicle.getUpdatedBy())
                .createdAt(vehicle.getCreatedAt() != null ? vehicle.getCreatedAt().toString() : null)
                .updatedAt(vehicle.getUpdatedAt() != null ? vehicle.getUpdatedAt().toString() : null)
                .build();
    }
}
