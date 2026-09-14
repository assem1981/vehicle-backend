package com.vehicle.backend.modules.vehicle.repository;

import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByHostId(Long hostId);
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByLocationCityContainingIgnoreCaseAndStatus(String city, VehicleStatus status);
    long countByStatus(VehicleStatus status);
}
