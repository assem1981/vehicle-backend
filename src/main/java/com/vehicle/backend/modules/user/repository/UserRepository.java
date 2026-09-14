package com.vehicle.backend.modules.user.repository;

import com.vehicle.backend.modules.user.entity.HostApprovalStatus;
import com.vehicle.backend.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByHostApprovalStatus(HostApprovalStatus status);
}
