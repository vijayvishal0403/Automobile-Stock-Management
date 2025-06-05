package com.stockmanage.automobile.service.impl;

import com.stockmanage.automobile.dto.MaintenanceDTO;
import com.stockmanage.automobile.model.Maintenance;
import com.stockmanage.automobile.model.Vehicle;
import com.stockmanage.automobile.repository.MaintenanceRepository;
import com.stockmanage.automobile.repository.VehicleRepository;
import com.stockmanage.automobile.service.MaintenanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository, 
                                  VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<MaintenanceDTO> getAllMaintenance() {
        return maintenanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceDTO getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance record not found with id: " + id));
    }

    @Override
    public List<MaintenanceDTO> getMaintenanceByVehicleId(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + vehicleId));
        
        return maintenanceRepository.findByVehicle(vehicle).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceDTO> getMaintenanceByStatus(Maintenance.MaintenanceStatus status) {
        return maintenanceRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceDTO> getMaintenanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findByServiceDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaintenanceDTO> getUpcomingMaintenance(LocalDate date) {
        return maintenanceRepository.findByNextServiceDateLessThanEqual(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaintenanceDTO createMaintenance(MaintenanceDTO maintenanceDTO) {
        Vehicle vehicle = vehicleRepository.findById(maintenanceDTO.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + maintenanceDTO.getVehicleId()));
        
        Maintenance maintenance = new Maintenance();
        maintenance.setVehicle(vehicle);
        maintenance.setMaintenanceType(maintenanceDTO.getMaintenanceType());
        maintenance.setServiceDate(maintenanceDTO.getServiceDate());
        maintenance.setNextServiceDate(maintenanceDTO.getNextServiceDate());
        maintenance.setCost(maintenanceDTO.getCost());
        maintenance.setDescription(maintenanceDTO.getDescription());
        maintenance.setServiceProvider(maintenanceDTO.getServiceProvider());
        maintenance.setStatus(maintenanceDTO.getStatus() != null ? 
                maintenanceDTO.getStatus() : Maintenance.MaintenanceStatus.SCHEDULED);
        
        maintenance = maintenanceRepository.save(maintenance);
        
        return convertToDTO(maintenance);
    }

    @Override
    @Transactional
    public MaintenanceDTO updateMaintenance(Long id, MaintenanceDTO maintenanceDTO) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance record not found with id: " + id));
        
        // Vehicle can't be changed, but other fields can
        maintenance.setMaintenanceType(maintenanceDTO.getMaintenanceType());
        maintenance.setServiceDate(maintenanceDTO.getServiceDate());
        maintenance.setNextServiceDate(maintenanceDTO.getNextServiceDate());
        maintenance.setCost(maintenanceDTO.getCost());
        maintenance.setDescription(maintenanceDTO.getDescription());
        maintenance.setServiceProvider(maintenanceDTO.getServiceProvider());
        maintenance.setStatus(maintenanceDTO.getStatus());
        
        maintenance = maintenanceRepository.save(maintenance);
        
        return convertToDTO(maintenance);
    }

    @Override
    @Transactional
    public void deleteMaintenance(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new EntityNotFoundException("Maintenance record not found with id: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MaintenanceDTO updateMaintenanceStatus(Long id, Maintenance.MaintenanceStatus status) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance record not found with id: " + id));
        
        maintenance.setStatus(status);
        maintenance = maintenanceRepository.save(maintenance);
        
        return convertToDTO(maintenance);
    }
    
    private MaintenanceDTO convertToDTO(Maintenance maintenance) {
        MaintenanceDTO dto = new MaintenanceDTO();
        dto.setId(maintenance.getId());
        dto.setVehicleId(maintenance.getVehicle().getId());
        
        Vehicle vehicle = maintenance.getVehicle();
        dto.setVehicleDetails(vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getVehicleYear() + ")");
        
        dto.setMaintenanceType(maintenance.getMaintenanceType());
        dto.setServiceDate(maintenance.getServiceDate());
        dto.setNextServiceDate(maintenance.getNextServiceDate());
        dto.setCost(maintenance.getCost());
        dto.setDescription(maintenance.getDescription());
        dto.setServiceProvider(maintenance.getServiceProvider());
        // Set mileageAtService to the vehicle's current mileage as a fallback
        dto.setMileageAtService(vehicle.getMileage());
        dto.setStatus(maintenance.getStatus());
        
        return dto;
    }

    private String generateMaintenanceNumber(Vehicle vehicle) {
        return String.format("MNT-%s-%d-%d",
            vehicle.getMake().substring(0, 3).toUpperCase(),
            vehicle.getVehicleYear(),
            System.currentTimeMillis() % 10000);
    }
} 