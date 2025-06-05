package com.stockmanage.automobile.repository;

import com.stockmanage.automobile.model.Order;
import com.stockmanage.automobile.model.OrderItem;
import com.stockmanage.automobile.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByVehicle(Vehicle vehicle);
    
    List<OrderItem> findByIsPaid(Boolean isPaid);
} 