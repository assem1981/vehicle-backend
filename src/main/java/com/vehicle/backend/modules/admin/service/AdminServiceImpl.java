package com.vehicle.backend.modules.admin.service;

import com.vehicle.backend.config.ActiveMQConfig;
import com.vehicle.backend.modules.admin.dto.AdminActionRequest;
import com.vehicle.backend.modules.admin.dto.HostApplicationResponse;
import com.vehicle.backend.modules.user.entity.HostApprovalStatus;
import com.vehicle.backend.modules.user.entity.Role;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JmsTemplate jmsTemplate;

    @Override
    public void setUserActiveStatus(AdminActionRequest request) {
        User user = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getTargetUserId()));

        user.setActive(request.getActive());
        userRepository.save(user);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "ADMIN_SET_USER_ACTIVE",
                "targetUserId", request.getTargetUserId(),
                "active", request.getActive(),
                "reason", request.getReason() != null ? request.getReason() : "N/A"
        ));
    }

    @Override
    public void deleteUserAccount(Long userId) {
        userRepository.deleteById(userId);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "ADMIN_DELETE_USER",
                "targetUserId", userId
        ));
    }

    @Override
    public List<HostApplicationResponse> getPendingHostApplications() {
        return userRepository.findByHostApprovalStatus(HostApprovalStatus.PENDING)
                .stream()
                .map(user -> HostApplicationResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .hostApprovalStatus(user.getHostApprovalStatus())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void approveHostApplication(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getHostApprovalStatus() != HostApprovalStatus.PENDING) {
            throw new IllegalStateException("Host application is not in PENDING state.");
        }

        user.setHostApprovalStatus(HostApprovalStatus.APPROVED);
        user.setActive(true);
        // Ensure ROLE_HOST is present
        user.getRoles().add(Role.ROLE_HOST);
        userRepository.save(user);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "ADMIN_APPROVE_HOST_APPLICATION",
                "targetUserId", userId,
                "email", user.getEmail()
        ));

        jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                "recipientEmail", user.getEmail(),
                "title", "Host Application Approved!",
                "message", "Congratulations! Your host application has been approved. You can now log in and start listing your vehicles."
        ));
    }

    @Override
    public void rejectHostApplication(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getHostApprovalStatus() != HostApprovalStatus.PENDING) {
            throw new IllegalStateException("Host application is not in PENDING state.");
        }

        user.setHostApprovalStatus(HostApprovalStatus.REJECTED);
        user.setActive(false);
        userRepository.save(user);

        jmsTemplate.convertAndSend(ActiveMQConfig.AUDIT_QUEUE, Map.of(
                "action", "ADMIN_REJECT_HOST_APPLICATION",
                "targetUserId", userId,
                "email", user.getEmail()
        ));

        jmsTemplate.convertAndSend(ActiveMQConfig.NOTIFICATION_QUEUE, Map.of(
                "recipientEmail", user.getEmail(),
                "title", "Host Application Update",
                "message", "We're sorry, but your host application has not been approved at this time. Please contact support for more information."
        ));
    }
}
