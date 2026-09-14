package com.vehicle.backend.modules.vehicle.controller;

import com.vehicle.backend.modules.vehicle.dto.VehicleDto;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import com.vehicle.backend.modules.vehicle.service.VehicleService;
import com.vehicle.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> createVehicle(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody VehicleDto vehicleDto) {
        return ResponseEntity.ok(vehicleService.createVehicle(userPrincipal.getId(), vehicleDto));
    }

    @GetMapping("/maintenance")
    public ResponseEntity<List<java.util.Map<String, Object>>> getMaintenanceRecords() {
        List<java.util.Map<String, Object>> mockRecords = List.of(
            java.util.Map.of(
                "id", "MNT-101",
                "vehicleId", 1,
                "vehicleName", "Toyota Camry (2023)",
                "serviceType", "Oil Change",
                "date", "2026-08-20",
                "odometerMiles", 15400,
                "costUSD", 75.00,
                "provider", "QuickLube Express",
                "notes", "Full synthetic 0W-20 oil change & filter replacement"
            ),
            java.util.Map.of(
                "id", "MNT-102",
                "vehicleId", 2,
                "vehicleName", "Honda Civic (2024)",
                "serviceType", "Tire Rotation",
                "date", "2026-08-10",
                "odometerMiles", 8200,
                "costUSD", 45.00,
                "provider", "City Auto Service",
                "notes", "Rotated tires and balanced front wheels"
            )
        );
        return ResponseEntity.ok(mockRecords);
    }

    @PostMapping("/maintenance")
    public ResponseEntity<java.util.Map<String, Object>> addMaintenanceRecord(@RequestBody java.util.Map<String, Object> record) {
        java.util.Map<String, Object> newRecord = new java.util.HashMap<>(record);
        newRecord.put("id", "MNT-" + System.currentTimeMillis());
        return ResponseEntity.ok(newRecord);
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<java.util.Map<String, Object>>> getVehicleDocuments(@PathVariable String id) {
        List<java.util.Map<String, Object>> docs = List.of(
            java.util.Map.of(
                "id", "DOC-201",
                "type", "REGISTRATION",
                "fileName", "state_registration_2026.pdf",
                "fileUrl", "/uploads/documents/registration.pdf",
                "status", "APPROVED",
                "uploadedAt", "2026-01-15"
            ),
            java.util.Map.of(
                "id", "DOC-202",
                "type", "INSURANCE",
                "fileName", "commercial_auto_policy.pdf",
                "fileUrl", "/uploads/documents/insurance.pdf",
                "status", "APPROVED",
                "uploadedAt", "2026-02-01"
            )
        );
        return ResponseEntity.ok(docs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @GetMapping("/host/me")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<List<VehicleDto>> getMyVehicles(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(vehicleService.getVehiclesByHost(userPrincipal.getId()));
    }

    @GetMapping("/host/{hostId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOST')")
    public ResponseEntity<List<VehicleDto>> getVehiclesByHostId(@PathVariable Long hostId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByHost(hostId));
    }

    @GetMapping
    public ResponseEntity<List<VehicleDto>> getAllAvailableVehicles() {
        return ResponseEntity.ok(vehicleService.getAllAvailableVehicles());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleDto>> getAllVehiclesForAdmin() {
        return ResponseEntity.ok(vehicleService.getAllVehiclesForAdmin());
    }

    @GetMapping(params = "status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleDto>> getVehiclesByStatus(@RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleDto> updateVehicleStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateVehicleStatus(id, status, userPrincipal.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOST')")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDto vehicleDto) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
