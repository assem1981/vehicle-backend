package com.vehicle.backend.modules.host.controller;

import com.vehicle.backend.modules.host.dto.HostDto;
import com.vehicle.backend.modules.host.service.HostService;
import com.vehicle.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequestMapping("/api/hosts")
@RequiredArgsConstructor
public class HostController {

    private final HostService hostService;

    @PostMapping("/register")
    public ResponseEntity<HostDto> registerHost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String payoutDetails) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(hostService.registerHost(userId, bio, payoutDetails));
    }

    @GetMapping("/me")
    public ResponseEntity<HostDto> getMyHostProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        try {
            return ResponseEntity.ok(hostService.getHostByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.ok(hostService.getHostById(userId));
        }
    }

    @GetMapping("/me/earnings")
    public ResponseEntity<Map<String, Object>> getMyEarnings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = (userPrincipal != null && userPrincipal.getId() != null) ? userPrincipal.getId() : 1L;
        return ResponseEntity.ok(hostService.getEarningsSummary(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HostDto> getHostById(@PathVariable Long id) {
        return ResponseEntity.ok(hostService.getHostById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HostDto>> getAllHosts() {
        return ResponseEntity.ok(hostService.getAllHosts());
    }
}
