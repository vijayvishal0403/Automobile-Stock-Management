package com.stockmanage.automobile.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@ToString(exclude = "order")  // Exclude order from toString to prevent recursion
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "price", nullable = false)
    private BigDecimal unitPrice;
    
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    @Column
    private String additionalServices;
    
    @Column(nullable = false)
    private Boolean isPaid = false;
    
    // Custom equals and hashCode that don't use the order field
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 