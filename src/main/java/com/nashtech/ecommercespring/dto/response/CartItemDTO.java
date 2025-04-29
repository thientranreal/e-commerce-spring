package com.nashtech.ecommercespring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private List<ProductImageDTO> productImages;
    private int quantity;
    private BigDecimal price;
}
