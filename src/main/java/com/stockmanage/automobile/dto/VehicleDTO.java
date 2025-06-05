package com.stockmanage.automobile.dto;

import com.stockmanage.automobile.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String make;
    private String model;
    private Integer vehicleYear;
    private String vin;
    private String color;
    private String price;
    private Integer mileage;
    private Vehicle.FuelType fuelType;
    private Vehicle.TransmissionType transmissionType;
    private String engineSize;
    private Boolean available;
    private LocalDate acquisitionDate;
    private String description;
    private String imageUrl;
} 