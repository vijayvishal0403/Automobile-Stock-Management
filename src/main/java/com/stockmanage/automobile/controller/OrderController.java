package com.stockmanage.automobile.controller;

import com.stockmanage.automobile.dto.OrderDTO;
import com.stockmanage.automobile.model.Order;
import com.stockmanage.automobile.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList()); // Return empty list instead of error
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error fetching order with id: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Order not found", "message", e.getMessage()));
        }
    }
    
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error fetching order with number: " + orderNumber, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Order not found", "message", e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders for user: " + userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders with status: " + status, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.emptyList());
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByDateRange(startDate, endDate);
            return ResponseEntity.ok(orders);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format for date range query", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid date format", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching orders in date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            logger.info("Received order creation request: " + orderDTO);
            
            // Validate the required fields
            if (orderDTO.getUserId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Validation failed", "message", "userId is required"));
            }
            
            if (orderDTO.getOrderDate() == null) {
                // Set current date if not provided
                orderDTO.setOrderDate(LocalDateTime.now());
            }
            
            if (orderDTO.getStatus() == null) {
                // Set default status if not provided
                orderDTO.setStatus(Order.OrderStatus.PENDING);
            }
            
            // Create the order
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error when creating order", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Validation failed", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Order creation failed", "message", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            logger.error("Error updating order with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Order update failed", "message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting order with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Order deletion failed", "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id, @PathVariable Order.OrderStatus status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            logger.error("Error updating status of order with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Status update failed", "message", e.getMessage()));
        }
    }
} 