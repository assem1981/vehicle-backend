package com.vehicle.backend.modules.verification.repository;

import com.vehicle.backend.modules.verification.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationRecord, Long> {
    Optional<VerificationRecord> findByUserId(Long userId);
}
