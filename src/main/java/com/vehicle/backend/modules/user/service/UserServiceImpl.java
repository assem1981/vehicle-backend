package com.vehicle.backend.modules.user.service;

import com.vehicle.backend.modules.user.dto.UserProfileResponse;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    // @Cacheable(value = "users", key = "'profile:' + #userId") // Disabled due to Redis deserialization issues
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return mapToDto(user);
    }

    @Override
    public UserProfileResponse getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return mapToDto(user);
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileResponse getAdminUser() {
        List<User> adminUsers = userRepository.findByRole(com.vehicle.backend.modules.user.entity.Role.ROLE_ADMIN);
        if (!adminUsers.isEmpty()) {
            return mapToDto(adminUsers.get(0));
        }
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null && u.getRoles().contains(com.vehicle.backend.modules.user.entity.Role.ROLE_ADMIN))
                .findFirst()
                .map(this::mapToDto)
                .orElseGet(() -> {
                    List<User> all = userRepository.findAll();
                    if (!all.isEmpty()) return mapToDto(all.get(0));
                    return UserProfileResponse.builder()
                            .id(1L)
                            .firstName("System")
                            .lastName("Admin")
                            .email("admin@vehicle.com")
                            .build();
                });
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public UserProfileResponse updateUserProfile(Long userId, String firstName, String lastName, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);

        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    private UserProfileResponse mapToDto(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .active(user.isActive())
                .hostApprovalStatus(user.getHostApprovalStatus())
                .emailUpdates(user.isEmailUpdates())
                .agreedToTerms(user.isAgreedToTerms())
                .termsAgreedAt(user.getTermsAgreedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
