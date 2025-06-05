package com.stockmanage.automobile.repository;

import com.stockmanage.automobile.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByMakeIgnoreCase(String make);
    
    List<Vehicle> findByModelIgnoreCase(String model);
    
    List<Vehicle> findByVehicleYear(Integer year);
    
    List<Vehicle> findByAvailable(Boolean available);
    
    List<Vehicle> findByFuelType(Vehicle.FuelType fuelType);
    
    List<Vehicle> findByTransmissionType(Vehicle.TransmissionType transmissionType);
    
    List<Vehicle> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Vehicle> findByMileageLessThan(Integer maxMileage);
    
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.make) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.model) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.vin) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Vehicle> searchVehicles(String searchTerm);
} 