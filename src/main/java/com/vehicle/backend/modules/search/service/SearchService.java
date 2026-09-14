package com.vehicle.backend.modules.search.service;

import com.vehicle.backend.modules.search.dto.SearchQueryDto;
import com.vehicle.backend.modules.vehicle.dto.VehicleDto;

import java.util.List;

public interface SearchService {
    List<VehicleDto> searchVehicles(SearchQueryDto query);
}
