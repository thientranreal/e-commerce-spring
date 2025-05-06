package com.nashtech.ecommercespring.repository;

import com.nashtech.ecommercespring.enums.OrderStatus;
import com.nashtech.ecommercespring.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);
    boolean existsByUserIdAndOrderItemsProductIdAndStatus(UUID userId, UUID productId, OrderStatus status);
}
