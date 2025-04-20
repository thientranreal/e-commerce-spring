package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.response.OrderDTO;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTO placeOrder(UUID userId);
    OrderDTO getOrderById(UUID orderId);
    List<OrderDTO> getOrdersByUser(UUID userId);
}
