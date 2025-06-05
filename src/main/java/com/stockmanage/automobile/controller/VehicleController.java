package com.stockmanage.automobile.controller;

import com.stockmanage.automobile.dto.VehicleDTO;
import com.stockmanage.automobile.model.Vehicle;
import com.stockmanage.automobile.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }
    
    @GetMapping
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }
    
    @PostMapping
    public ResponseEntity<?> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        try {
            // Validate required fields
            if (vehicleDTO.getMake() == null || vehicleDTO.getMake().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Make is required");
            }
            if (vehicleDTO.getModel() == null || vehicleDTO.getModel().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Model is required");
            }
            if (vehicleDTO.getVehicleYear() == null) {
                return ResponseEntity.badRequest().body("Year is required");
            }
            if (vehicleDTO.getVin() == null || vehicleDTO.getVin().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("VIN is required");
            }
            if (vehicleDTO.getColor() == null || vehicleDTO.getColor().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Color is required");
            }
            if (vehicleDTO.getPrice() == null || vehicleDTO.getPrice().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Price is required");
            }
            if (vehicleDTO.getMileage() == null) {
                return ResponseEntity.badRequest().body("Mileage is required");
            }
            if (vehicleDTO.getFuelType() == null) {
                return ResponseEntity.badRequest().body("Fuel type is required");
            }
            if (vehicleDTO.getTransmissionType() == null) {
                return ResponseEntity.badRequest().body("Transmission type is required");
            }
            if (vehicleDTO.getEngineSize() == null || vehicleDTO.getEngineSize().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Engine size is required");
            }

            VehicleDTO createdVehicle = vehicleService.createVehicle(vehicleDTO);
            return new ResponseEntity<>(createdVehicle, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating vehicle: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Conflict");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal server error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/make/{make}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByMake(@PathVariable String make) {
        return ResponseEntity.ok(vehicleService.findVehiclesByMake(make));
    }
    
    @GetMapping("/model/{model}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByModel(@PathVariable String model) {
        return ResponseEntity.ok(vehicleService.findVehiclesByModel(model));
    }
    
    @GetMapping("/year/{year}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(vehicleService.findVehiclesByYear(year));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<VehicleDTO>> getAvailableVehicles() {
        return ResponseEntity.ok(vehicleService.findVehiclesByAvailability(true));
    }
    
    @GetMapping("/fuel-type/{fuelType}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByFuelType(@PathVariable Vehicle.FuelType fuelType) {
        return ResponseEntity.ok(vehicleService.findVehiclesByFuelType(fuelType));
    }
    
    @GetMapping("/transmission-type/{transmissionType}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByTransmissionType(@PathVariable Vehicle.TransmissionType transmissionType) {
        return ResponseEntity.ok(vehicleService.findVehiclesByTransmissionType(transmissionType));
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByPriceRange(
            @RequestParam String minPrice, @RequestParam String maxPrice) {
        return ResponseEntity.ok(vehicleService.findVehiclesByPriceRange(minPrice, maxPrice));
    }
    
    @GetMapping("/max-mileage/{maxMileage}")
    public ResponseEntity<List<VehicleDTO>> getVehiclesByMaxMileage(@PathVariable Integer maxMileage) {
        return ResponseEntity.ok(vehicleService.findVehiclesByMaxMileage(maxMileage));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<VehicleDTO>> searchVehicles(@RequestParam String searchTerm) {
        return ResponseEntity.ok(vehicleService.searchVehicles(searchTerm));
    }
} 