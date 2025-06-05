package com.stockmanage.automobile.service;

import com.stockmanage.automobile.dto.VehicleDTO;
import com.stockmanage.automobile.model.Vehicle;

import java.util.List;

public interface VehicleService {
    
    List<VehicleDTO> getAllVehicles();
    
    VehicleDTO getVehicleById(Long id);
    
    VehicleDTO createVehicle(VehicleDTO vehicleDTO);
    
    VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO);
    
    void deleteVehicle(Long id);
    
    List<VehicleDTO> findVehiclesByMake(String make);
    
    List<VehicleDTO> findVehiclesByModel(String model);
    
    List<VehicleDTO> findVehiclesByYear(Integer year);
    
    List<VehicleDTO> findVehiclesByAvailability(Boolean available);
    
    List<VehicleDTO> findVehiclesByFuelType(Vehicle.FuelType fuelType);
    
    List<VehicleDTO> findVehiclesByTransmissionType(Vehicle.TransmissionType transmissionType);
    
    List<VehicleDTO> findVehiclesByPriceRange(String minPrice, String maxPrice);
    
    List<VehicleDTO> findVehiclesByMaxMileage(Integer maxMileage);
    
    List<VehicleDTO> searchVehicles(String searchTerm);
} 