package com.vehicle.backend.modules.vehicle.service;

import com.vehicle.backend.modules.vehicle.dto.VehicleDto;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;

import java.util.List;

public interface VehicleService {
    VehicleDto createVehicle(Long hostId, VehicleDto vehicleDto);
    VehicleDto getVehicleById(Long id);
    List<VehicleDto> getVehiclesByHost(Long hostId);
    List<VehicleDto> getAllAvailableVehicles();
    List<VehicleDto> getAllVehiclesForAdmin();
    List<VehicleDto> getVehiclesByStatus(VehicleStatus status);
    VehicleDto updateVehicleStatus(Long id, VehicleStatus status, Long updatedByUserId);
    VehicleDto updateVehicle(Long id, VehicleDto vehicleDto);
    void deleteVehicle(Long id);
}
