package com.nashtech.ecommercespring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartDTO {
    private UUID id;
    private UUID userId;
    private List<CartItemDTO> cartItems;
}
