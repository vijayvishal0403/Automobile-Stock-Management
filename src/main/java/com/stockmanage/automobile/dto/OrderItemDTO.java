package com.stockmanage.automobile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long orderId;
    private Long vehicleId;
    private String vehicleDetails;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String additionalServices;
    private Boolean isPaid;
} 