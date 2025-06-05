package com.stockmanage.automobile.dto;

import com.stockmanage.automobile.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String customerName;
    private Long vehicleId;
    private String vehicleDetails;
    private LocalDateTime orderDate;
    private Order.OrderStatus status;
    private BigDecimal totalAmount;
    private String notes;
    private String paymentMethod;
    private LocalDateTime deliveryDate;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> orderItems;
} 