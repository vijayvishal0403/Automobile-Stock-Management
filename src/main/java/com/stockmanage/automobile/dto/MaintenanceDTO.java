package com.stockmanage.automobile.dto;

import com.stockmanage.automobile.model.Maintenance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceDTO {
    private Long id;
    private Long vehicleId;
    private String vehicleDetails;
    private String maintenanceType;
    private LocalDate serviceDate;
    private LocalDate nextServiceDate;
    private BigDecimal cost;
    private String description;
    private String serviceProvider;
    private Integer mileageAtService;
    private Maintenance.MaintenanceStatus status;
} 