package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.response.CartDTO;

import java.util.UUID;

public interface CartService {
    CartDTO getCart(UUID userId);
    CartDTO addItemToCart(UUID userId, UUID productId);
    void removeItemFromCart(UUID userId, UUID productId);
    void deleteCart(UUID cartId);
    CartDTO updateQuantity(UUID userId, UUID productId, int quantity);
}
