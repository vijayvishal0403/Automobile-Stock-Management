package com.stockmanage.automobile.service;

import com.stockmanage.automobile.dto.MaintenanceDTO;
import com.stockmanage.automobile.model.Maintenance;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceService {
    
    List<MaintenanceDTO> getAllMaintenance();
    
    MaintenanceDTO getMaintenanceById(Long id);
    
    List<MaintenanceDTO> getMaintenanceByVehicleId(Long vehicleId);
    
    List<MaintenanceDTO> getMaintenanceByStatus(Maintenance.MaintenanceStatus status);
    
    List<MaintenanceDTO> getMaintenanceByDateRange(LocalDate startDate, LocalDate endDate);
    
    List<MaintenanceDTO> getUpcomingMaintenance(LocalDate date);
    
    MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO);
    
    MaintenanceDTO updateMaintenance(Long id, MaintenanceDTO maintenanceDTO);
    
    void deleteMaintenance(Long id);
    
    MaintenanceDTO updateMaintenanceStatus(Long id, Maintenance.MaintenanceStatus status);
} 