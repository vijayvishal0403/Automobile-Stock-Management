package com.stockmanage.automobile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private String maintenanceType;
    
    @Column(nullable = false)
    private LocalDate serviceDate;
    
    @Column
    private LocalDate nextServiceDate;
    
    @Column(nullable = false)
    private BigDecimal cost;
    
    @Column(name = "notes", length = 1000)
    private String description;
    
    @Column
    private String serviceProvider;
    
    @Transient
    private Integer mileageAtService;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;
    
    public enum MaintenanceStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
} 