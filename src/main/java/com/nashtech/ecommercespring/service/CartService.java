package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.CartItemReqDTO;
import com.nashtech.ecommercespring.dto.response.CartDTO;

import java.util.UUID;

public interface CartService {
    CartDTO getCart(UUID userId);
    CartDTO addItemToCart(CartItemReqDTO reqDTO);
    void removeItemFromCart(CartItemReqDTO reqDTO);
    void deleteCart(UUID cartId);
    CartDTO updateQuantity(CartItemReqDTO reqDTO);
}
