package com.stockmanage.automobile.repository;

import com.stockmanage.automobile.model.Maintenance;
import com.stockmanage.automobile.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    
    List<Maintenance> findByVehicle(Vehicle vehicle);
    
    List<Maintenance> findByStatus(Maintenance.MaintenanceStatus status);
    
    List<Maintenance> findByServiceDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Maintenance> findByNextServiceDateLessThanEqual(LocalDate date);
    
    List<Maintenance> findByVehicleAndStatus(Vehicle vehicle, Maintenance.MaintenanceStatus status);
} 