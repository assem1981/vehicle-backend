package com.vehicle.backend.modules.admin.service;

import com.vehicle.backend.modules.admin.dto.AdminActionRequest;
import com.vehicle.backend.modules.admin.dto.HostApplicationResponse;

import java.util.List;

public interface AdminService {
    void setUserActiveStatus(AdminActionRequest request);
    void deleteUserAccount(Long userId);
    List<HostApplicationResponse> getPendingHostApplications();
    void approveHostApplication(Long userId);
    void rejectHostApplication(Long userId);
}
