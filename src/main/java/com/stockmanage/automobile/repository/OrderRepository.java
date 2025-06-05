package com.stockmanage.automobile.repository;

import com.stockmanage.automobile.model.Order;
import com.stockmanage.automobile.model.User;
import com.stockmanage.automobile.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUser(User user);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
    
    List<Order> findByVehicle(Vehicle vehicle);
} 