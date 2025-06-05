package com.stockmanage.automobile.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString(exclude = "orderItems")  // Exclude orderItems from toString to prevent recursion
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = true)
    private Vehicle vehicle;
    
    @OneToMany(
        mappedBy = "order", 
        cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<OrderItem> orderItems = new HashSet<>();
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Column
    private String notes;
    
    @Column
    private String paymentMethod;
    
    @Column
    private LocalDateTime deliveryDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum OrderStatus {
        PENDING, PROCESSING, CONFIRMED, DELIVERED, CANCELLED
    }
    
    // Helper method to add an order item
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
    
    // Helper method to remove an order item
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
    
    // Custom equals and hashCode that don't use the orderItems field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 