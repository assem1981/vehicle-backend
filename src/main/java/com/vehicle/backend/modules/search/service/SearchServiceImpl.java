package com.vehicle.backend.modules.search.service;

import com.vehicle.backend.modules.search.dto.SearchQueryDto;
import com.vehicle.backend.modules.vehicle.dto.VehicleDto;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Cacheable(value = "search_vehicles", key = "{#query.city, #query.category, #query.maxPrice, #query.startDate, #query.endDate}")
    public List<VehicleDto> searchVehicles(SearchQueryDto query) {
        var vehicles = (query.getCity() != null && !query.getCity().isBlank())
                ? vehicleRepository.findByLocationCityContainingIgnoreCaseAndStatus(query.getCity(), VehicleStatus.AVAILABLE)
                : vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);

        return vehicles.stream()
                .filter(v -> query.getMaxPrice() == null || (v.getDailyRate() != null && v.getDailyRate().compareTo(query.getMaxPrice()) <= 0))
                .filter(v -> query.getCategory() == null || query.getCategory().isBlank() || query.getCategory().equalsIgnoreCase(v.getCategory()))
                .map(v -> VehicleDto.builder()
                        .id(v.getId())
                        .hostId(v.getHostId())
                        .make(v.getMake())
                        .model(v.getModel())
                        .year(v.getYear())
                        .vin(v.getVin())
                        .category(v.getCategory())
                        .transmission(v.getTransmission())
                        .seatingCapacity(v.getSeatingCapacity())
                        .fuelType(v.getFuelType())
                        .dailyRate(v.getDailyRate())
                        .locationCity(v.getLocationCity())
                        .latitude(v.getLatitude())
                        .longitude(v.getLongitude())
                        .status(v.getStatus())
                        .imageUrl(v.getImageUrl())
                        .interiorImageUrl(v.getInteriorImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
