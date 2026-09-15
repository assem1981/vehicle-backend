package com.vehicle.backend.modules.user.controller;

import com.vehicle.backend.modules.user.dto.UserProfileResponse;
import com.vehicle.backend.modules.user.service.UserService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || userPrincipal.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getUserProfile(userPrincipal.getId()));
    }

    @GetMapping("/admin-contact")
    public ResponseEntity<UserProfileResponse> getAdminContact() {
        return ResponseEntity.ok(userService.getAdminUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber) {
        if (userPrincipal == null || userPrincipal.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.updateUserProfile(userPrincipal.getId(), firstName, lastName, phoneNumber));
    }
}
