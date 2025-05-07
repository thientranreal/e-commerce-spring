package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTO placeOrder(UUID userId, List<UUID> productIds);
    OrderDTO getOrderById(UUID orderId);
    List<OrderDTO> getOrdersByUser(UUID userId);
    OrderDTO updateStatusById(UUID orderId, OrderStatus status);
}
