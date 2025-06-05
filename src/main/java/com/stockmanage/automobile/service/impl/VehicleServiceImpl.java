package com.stockmanage.automobile.service.impl;

import com.stockmanage.automobile.dto.VehicleDTO;
import com.stockmanage.automobile.model.OrderItem;
import com.stockmanage.automobile.model.Vehicle;
import com.stockmanage.automobile.repository.OrderItemRepository;
import com.stockmanage.automobile.repository.OrderRepository;
import com.stockmanage.automobile.repository.VehicleRepository;
import com.stockmanage.automobile.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, 
                             OrderItemRepository orderItemRepository,
                             OrderRepository orderRepository) {
        this.vehicleRepository = vehicleRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleDTO getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));
    }

    @Override
    @Transactional
    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        Vehicle vehicle = convertToEntity(vehicleDTO);
        vehicle = vehicleRepository.save(vehicle);
        return convertToDTO(vehicle);
    }

    @Override
    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));
        
        updateVehicleFromDTO(vehicle, vehicleDTO);
        vehicle = vehicleRepository.save(vehicle);
        return convertToDTO(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));
        
        // Check if vehicle is referenced in any orders directly
        if (orderRepository.findByVehicle(vehicle).size() > 0) {
            throw new IllegalStateException("Cannot delete vehicle as it is referenced in orders. Mark as unavailable instead.");
        }
        
        // Check if vehicle is referenced in any order items
        List<OrderItem> orderItems = orderItemRepository.findByVehicle(vehicle);
        if (!orderItems.isEmpty()) {
            throw new IllegalStateException("Cannot delete vehicle as it is referenced in order items. Mark as unavailable instead.");
        }
        
        vehicleRepository.delete(vehicle);
    }

    @Override
    public List<VehicleDTO> findVehiclesByMake(String make) {
        return vehicleRepository.findByMakeIgnoreCase(make).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByModel(String model) {
        return vehicleRepository.findByModelIgnoreCase(model).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByYear(Integer year) {
        return vehicleRepository.findByVehicleYear(year).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByAvailability(Boolean available) {
        return vehicleRepository.findByAvailable(available).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByFuelType(Vehicle.FuelType fuelType) {
        return vehicleRepository.findByFuelType(fuelType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByTransmissionType(Vehicle.TransmissionType transmissionType) {
        return vehicleRepository.findByTransmissionType(transmissionType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByPriceRange(String minPrice, String maxPrice) {
        return vehicleRepository.findByPriceBetween(
            new BigDecimal(minPrice),
            new BigDecimal(maxPrice)
        ).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> findVehiclesByMaxMileage(Integer maxMileage) {
        return vehicleRepository.findByMileageLessThan(maxMileage).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleDTO> searchVehicles(String searchTerm) {
        return vehicleRepository.searchVehicles(searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setVehicleYear(vehicle.getVehicleYear());
        dto.setVin(vehicle.getVin());
        dto.setColor(vehicle.getColor());
        dto.setPrice(vehicle.getPrice().toString());
        dto.setMileage(vehicle.getMileage());
        dto.setFuelType(vehicle.getFuelType());
        dto.setTransmissionType(vehicle.getTransmissionType());
        dto.setEngineSize(vehicle.getEngineSize());
        dto.setAvailable(vehicle.getAvailable());
        dto.setAcquisitionDate(vehicle.getAcquisitionDate());
        dto.setDescription(vehicle.getDescription());
        dto.setImageUrl(vehicle.getImageUrl());
        return dto;
    }

    private Vehicle convertToEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(dto.getMake());
        vehicle.setModel(dto.getModel());
        vehicle.setVehicleYear(dto.getVehicleYear());
        vehicle.setVin(dto.getVin());
        vehicle.setColor(dto.getColor());
        vehicle.setPrice(new BigDecimal(dto.getPrice()));
        vehicle.setMileage(dto.getMileage());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setTransmissionType(dto.getTransmissionType());
        vehicle.setEngineSize(dto.getEngineSize());
        vehicle.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : true);
        vehicle.setAcquisitionDate(dto.getAcquisitionDate() != null ? dto.getAcquisitionDate() : LocalDate.now());
        vehicle.setDescription(dto.getDescription());
        vehicle.setImageUrl(dto.getImageUrl());
        return vehicle;
    }

    private void updateVehicleFromDTO(Vehicle vehicle, VehicleDTO dto) {
        if (dto.getMake() != null) vehicle.setMake(dto.getMake());
        if (dto.getModel() != null) vehicle.setModel(dto.getModel());
        if (dto.getVehicleYear() != null) vehicle.setVehicleYear(dto.getVehicleYear());
        if (dto.getVin() != null) vehicle.setVin(dto.getVin());
        if (dto.getColor() != null) vehicle.setColor(dto.getColor());
        if (dto.getPrice() != null) vehicle.setPrice(new BigDecimal(dto.getPrice()));
        if (dto.getMileage() != null) vehicle.setMileage(dto.getMileage());
        if (dto.getFuelType() != null) vehicle.setFuelType(dto.getFuelType());
        if (dto.getTransmissionType() != null) vehicle.setTransmissionType(dto.getTransmissionType());
        if (dto.getEngineSize() != null) vehicle.setEngineSize(dto.getEngineSize());
        if (dto.getAvailable() != null) vehicle.setAvailable(dto.getAvailable());
        if (dto.getAcquisitionDate() != null) vehicle.setAcquisitionDate(dto.getAcquisitionDate());
        if (dto.getDescription() != null) vehicle.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) vehicle.setImageUrl(dto.getImageUrl());
    }
} 