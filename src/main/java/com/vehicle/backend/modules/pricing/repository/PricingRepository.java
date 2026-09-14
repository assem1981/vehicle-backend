package com.vehicle.backend.modules.pricing.repository;

import com.vehicle.backend.modules.pricing.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findByVehicleId(Long vehicleId);
}
