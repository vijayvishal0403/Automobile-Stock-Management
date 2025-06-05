package com.stockmanage.automobile.service;

import com.stockmanage.automobile.dto.OrderDTO;
import com.stockmanage.automobile.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    
    List<OrderDTO> getAllOrders();
    
    OrderDTO getOrderById(Long id);
    
    OrderDTO getOrderByOrderNumber(String orderNumber);
    
    List<OrderDTO> getOrdersByUserId(Long userId);
    
    List<OrderDTO> getOrdersByStatus(Order.OrderStatus status);
    
    List<OrderDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    OrderDTO createOrder(OrderDTO orderDTO);
    
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);
    
    void deleteOrder(Long id);
    
    OrderDTO updateOrderStatus(Long id, Order.OrderStatus status);
} 