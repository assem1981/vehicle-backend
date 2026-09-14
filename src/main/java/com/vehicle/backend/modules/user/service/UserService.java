package com.vehicle.backend.modules.user.service;

import com.vehicle.backend.modules.user.dto.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse getUserProfileByEmail(String email);
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse updateUserProfile(Long userId, String firstName, String lastName, String phoneNumber);
}
