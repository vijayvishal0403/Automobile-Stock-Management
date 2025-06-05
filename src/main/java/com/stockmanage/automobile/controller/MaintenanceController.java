package com.stockmanage.automobile.controller;

import com.stockmanage.automobile.dto.MaintenanceDTO;
import com.stockmanage.automobile.model.Maintenance;
import com.stockmanage.automobile.service.MaintenanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceController.class);
    
    private final MaintenanceService maintenanceService;
    
    @Autowired
    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllMaintenance() {
        try {
            List<MaintenanceDTO> maintenance = maintenanceService.getAllMaintenance();
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            logger.error("Error fetching all maintenance records", e);
            return ResponseEntity.ok(Collections.emptyList()); // Return empty list instead of error
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMaintenanceById(@PathVariable Long id) {
        try {
            MaintenanceDTO maintenance = maintenanceService.getMaintenanceById(id);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            logger.error("Error fetching maintenance record with id: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Maintenance record not found", "message", e.getMessage()));
        }
    }
    
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getMaintenanceByVehicleId(@PathVariable Long vehicleId) {
        try {
            List<MaintenanceDTO> maintenance = maintenanceService.getMaintenanceByVehicleId(vehicleId);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            logger.error("Error fetching maintenance records for vehicle: " + vehicleId, e);
            return ResponseEntity.ok(Collections.emptyList()); // Return empty list instead of error
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getMaintenanceByStatus(@PathVariable Maintenance.MaintenanceStatus status) {
        try {
            List<MaintenanceDTO> maintenance = maintenanceService.getMaintenanceByStatus(status);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            logger.error("Error fetching maintenance records with status: " + status, e);
            return ResponseEntity.ok(Collections.emptyList()); // Return empty list instead of error
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getMaintenanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<MaintenanceDTO> maintenance = maintenanceService.getMaintenanceByDateRange(startDate, endDate);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            logger.error("Error fetching maintenance records in date range", e);
            return ResponseEntity.ok(Collections.emptyList()); // Return empty list instead of error
        }
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingMaintenance(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<MaintenanceDTO> maintenance = maintenanceService.getUpcomingMaintenance(date);
            return ResponseEntity.ok(maintenance);
        } catch (Exception e) {
            logger.error("Error fetching upcoming maintenance records", e);
            return ResponseEntity.ok(Collections.emptyList()); // Return empty list instead of error
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createMaintenance(@RequestBody MaintenanceDTO maintenanceDTO) {
        try {
            MaintenanceDTO created = maintenanceService.createMaintenance(maintenanceDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating maintenance record", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create maintenance record");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable Long id, @RequestBody MaintenanceDTO maintenanceDTO) {
        try {
            MaintenanceDTO updated = maintenanceService.updateMaintenance(id, maintenanceDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating maintenance record with id: " + id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update maintenance record");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Long id) {
        try {
            maintenanceService.deleteMaintenance(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting maintenance record with id: " + id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete maintenance record");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<?> updateMaintenanceStatus(
            @PathVariable Long id, @PathVariable Maintenance.MaintenanceStatus status) {
        try {
            MaintenanceDTO updated = maintenanceService.updateMaintenanceStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating status of maintenance record with id: " + id, e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update maintenance status");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
} 