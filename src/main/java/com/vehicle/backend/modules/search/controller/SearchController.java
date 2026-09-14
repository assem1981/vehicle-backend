package com.vehicle.backend.modules.search.controller;

import com.vehicle.backend.modules.search.dto.SearchQueryDto;
import com.vehicle.backend.modules.search.service.SearchService;
import com.vehicle.backend.modules.vehicle.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<List<VehicleDto>> searchVehicles(@RequestBody SearchQueryDto query) {
        return ResponseEntity.ok(searchService.searchVehicles(query));
    }
}
