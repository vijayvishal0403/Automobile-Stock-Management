package com.stockmanage.automobile.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String make;
    
    @Column(nullable = false)
    private String model;
    
    @Column(name = "vehicle_year", nullable = false)
    private Integer vehicleYear;
    
    @Column(nullable = false, unique = true)
    private String vin;
    
    @Column(nullable = false)
    private String color;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer mileage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmissionType;
    
    @Column(nullable = false)
    private String engineSize;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column
    private LocalDate acquisitionDate;
    
    @Column(length = 1000)
    private String description;
    
    @Column
    private String imageUrl;
    
    public enum FuelType {
        PETROL, DIESEL, ELECTRIC, HYBRID, LPG
    }
    
    public enum TransmissionType {
        MANUAL, AUTOMATIC, SEMI_AUTOMATIC
    }
    
    // Custom equals and hashCode using id as the only field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 