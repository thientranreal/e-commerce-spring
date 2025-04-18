package com.nashtech.ecommercespring.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartItemReqDTO {
    private UUID productId;
    private int quantity;
}
