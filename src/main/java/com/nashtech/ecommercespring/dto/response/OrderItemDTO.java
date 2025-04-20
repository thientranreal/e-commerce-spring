package com.nashtech.ecommercespring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}
