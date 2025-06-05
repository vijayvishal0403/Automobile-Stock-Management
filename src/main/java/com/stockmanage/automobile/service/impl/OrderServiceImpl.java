package com.stockmanage.automobile.service.impl;

import com.stockmanage.automobile.dto.OrderDTO;
import com.stockmanage.automobile.dto.OrderItemDTO;
import com.stockmanage.automobile.model.Order;
import com.stockmanage.automobile.model.OrderItem;
import com.stockmanage.automobile.model.User;
import com.stockmanage.automobile.model.Vehicle;
import com.stockmanage.automobile.repository.OrderItemRepository;
import com.stockmanage.automobile.repository.OrderRepository;
import com.stockmanage.automobile.repository.UserRepository;
import com.stockmanage.automobile.repository.VehicleRepository;
import com.stockmanage.automobile.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, 
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            VehicleRepository vehicleRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @Override
    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with order number: " + orderNumber));
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        
        return orderRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            // Log input data for debugging
            System.out.println("Creating order with DTO: " + orderDTO);
            
            // Validate required fields
            if (orderDTO.getUserId() == null) {
                throw new IllegalArgumentException("userId is required");
            }
            
            User user = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + orderDTO.getUserId()));
            
            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setUser(user);
            
            // Set order date, either from DTO or current time
            if (orderDTO.getOrderDate() != null) {
                order.setOrderDate(orderDTO.getOrderDate());
            } else {
                order.setOrderDate(LocalDateTime.now());
            }
            
            // Set status, default to PENDING if not specified
            if (orderDTO.getStatus() != null) {
                order.setStatus(orderDTO.getStatus());
            } else {
                order.setStatus(Order.OrderStatus.PENDING);
            }
            
            order.setTotalAmount(BigDecimal.ZERO);
            order.setNotes(orderDTO.getNotes());
            order.setPaymentMethod(orderDTO.getPaymentMethod());
            order.setDeliveryDate(orderDTO.getDeliveryDate());
            order.setCreatedAt(LocalDateTime.now());
            
            // Set vehicle if vehicleId is provided
            if (orderDTO.getVehicleId() != null) {
                try {
                    Vehicle vehicle = vehicleRepository.findById(orderDTO.getVehicleId())
                            .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + orderDTO.getVehicleId()));
                    order.setVehicle(vehicle);
                } catch (Exception e) {
                    System.err.println("Error setting vehicle: " + e.getMessage());
                    // Continue without vehicle if there's an error
                }
            }
            
            // First save to generate ID
            order = orderRepository.save(order);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            // Process order items if available
            if (orderDTO.getOrderItems() != null && !orderDTO.getOrderItems().isEmpty()) {
                for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
                    try {
                        if (itemDTO.getVehicleId() == null) {
                            System.err.println("Skipping order item with null vehicleId");
                            continue;
                        }
                        
                        Vehicle vehicle = vehicleRepository.findById(itemDTO.getVehicleId())
                                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + itemDTO.getVehicleId()));
                        
                        // Create order item and set all required fields
                        OrderItem orderItem = new OrderItem();
                        orderItem.setVehicle(vehicle);
                        orderItem.setQuantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1);
                        orderItem.setUnitPrice(vehicle.getPrice());
                        orderItem.setSubtotal(vehicle.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
                        orderItem.setAdditionalServices(itemDTO.getAdditionalServices());
                        orderItem.setIsPaid(itemDTO.getIsPaid() != null ? itemDTO.getIsPaid() : false);
                        
                        // Add using helper method
                        order.addOrderItem(orderItem);
                        
                        totalAmount = totalAmount.add(orderItem.getSubtotal());
                        
                        // Mark vehicle as unavailable if it's purchased
                        if (orderItem.getQuantity() > 0) {
                            vehicle.setAvailable(false);
                            vehicleRepository.save(vehicle);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing order item: " + e.getMessage());
                        // Continue with next item if there's an error
                    }
                }
            } else {
                // If no order items, but we have a vehicleId, create an implicit order item
                if (orderDTO.getVehicleId() != null) {
                    try {
                        Vehicle vehicle = vehicleRepository.findById(orderDTO.getVehicleId())
                                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + orderDTO.getVehicleId()));
                        
                        OrderItem orderItem = new OrderItem();
                        orderItem.setVehicle(vehicle);
                        orderItem.setQuantity(1);
                        orderItem.setUnitPrice(vehicle.getPrice());
                        orderItem.setSubtotal(vehicle.getPrice());
                        orderItem.setIsPaid(false);
                        
                        // Add using helper method
                        order.addOrderItem(orderItem);
                        
                        totalAmount = vehicle.getPrice();
                        
                        // Mark vehicle as unavailable
                        vehicle.setAvailable(false);
                        vehicleRepository.save(vehicle);
                    } catch (Exception e) {
                        System.err.println("Error creating implicit order item: " + e.getMessage());
                    }
                }
            }
            
            order.setTotalAmount(totalAmount);
            
            // Save order again with associated order items
            order = orderRepository.save(order);
            
            return convertToDTO(order);
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        
        // Only allow certain fields to be updated
        order.setStatus(orderDTO.getStatus());
        order.setNotes(orderDTO.getNotes());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setDeliveryDate(orderDTO.getDeliveryDate());
        
        order = orderRepository.save(order);
        
        return convertToDTO(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        
        // Make vehicles available again
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItem item : orderItems) {
            Vehicle vehicle = item.getVehicle();
            vehicle.setAvailable(true);
            vehicleRepository.save(vehicle);
        }
        
        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        
        order.setStatus(status);
        order = orderRepository.save(order);
        
        return convertToDTO(order);
    }
    
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateOrderNumber(Vehicle vehicle) {
        return String.format("%s-%d-%d", 
            vehicle.getMake().substring(0, 3).toUpperCase(),
            vehicle.getVehicleYear(),
            System.currentTimeMillis() % 10000);
    }
    
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setCustomerName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setNotes(order.getNotes());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setCreatedAt(order.getCreatedAt());
        
        // Add vehicle details if available
        if (order.getVehicle() != null) {
            Vehicle vehicle = order.getVehicle();
            dto.setVehicleId(vehicle.getId());
            dto.setVehicleDetails(vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getVehicleYear() + ")");
        }
        
        List<OrderItemDTO> orderItemDTOs = orderItemRepository.findByOrder(order).stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
        
        dto.setOrderItems(orderItemDTOs);
        
        return dto;
    }
    
    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder().getId());
        dto.setVehicleId(orderItem.getVehicle().getId());
        
        Vehicle vehicle = orderItem.getVehicle();
        dto.setVehicleDetails(vehicle.getMake() + " " + vehicle.getModel() + " (" + vehicle.getVehicleYear() + ")");
        
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setSubtotal(orderItem.getSubtotal());
        dto.setAdditionalServices(orderItem.getAdditionalServices());
        dto.setIsPaid(orderItem.getIsPaid());
        
        return dto;
    }
} 