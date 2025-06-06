package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartItemReqDTO {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID productId;

    private int quantity;
}
