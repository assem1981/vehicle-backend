package com.vehicle.backend.modules.user.dto;

import com.vehicle.backend.modules.user.entity.HostApprovalStatus;
import com.vehicle.backend.modules.user.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Set<Role> roles;
    private boolean active;
    private HostApprovalStatus hostApprovalStatus;
    private boolean emailUpdates;
    private boolean agreedToTerms;
    private LocalDateTime termsAgreedAt;
    private LocalDateTime createdAt;
}
