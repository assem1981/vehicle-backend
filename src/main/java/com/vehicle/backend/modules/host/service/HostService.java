package com.vehicle.backend.modules.host.service;

import com.vehicle.backend.modules.host.dto.HostDto;

import java.util.List;
import java.util.Map;

public interface HostService {
    HostDto registerHost(Long userId, String bio, String payoutAccountDetails);
    HostDto getHostByUserId(Long userId);
    HostDto getHostById(Long id);
    List<HostDto> getAllHosts();
    HostDto updatePayoutDetails(Long userId, String payoutDetails);
    Map<String, Object> getEarningsSummary(Long hostUserId);
}
