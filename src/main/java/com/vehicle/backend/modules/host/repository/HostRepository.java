package com.vehicle.backend.modules.host.repository;

import com.vehicle.backend.modules.host.entity.HostProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostRepository extends JpaRepository<HostProfile, Long> {
    Optional<HostProfile> findByUserId(Long userId);
    Boolean existsByUserId(Long userId);
}
