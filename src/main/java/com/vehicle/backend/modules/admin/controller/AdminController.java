package com.vehicle.backend.modules.admin.controller;

import com.vehicle.backend.modules.admin.dto.AdminActionRequest;
import com.vehicle.backend.modules.admin.dto.HostApplicationResponse;
import com.vehicle.backend.modules.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users/status")
    public ResponseEntity<Void> setUserStatus(@RequestBody AdminActionRequest request) {
        adminService.setUserActiveStatus(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUserAccount(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/host-applications")
    public ResponseEntity<List<HostApplicationResponse>> getPendingHostApplications() {
        return ResponseEntity.ok(adminService.getPendingHostApplications());
    }

    @PostMapping("/host-applications/{userId}/approve")
    public ResponseEntity<Void> approveHostApplication(@PathVariable Long userId) {
        adminService.approveHostApplication(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/host-applications/{userId}/reject")
    public ResponseEntity<Void> rejectHostApplication(@PathVariable Long userId) {
        adminService.rejectHostApplication(userId);
        return ResponseEntity.ok().build();
    }
}
