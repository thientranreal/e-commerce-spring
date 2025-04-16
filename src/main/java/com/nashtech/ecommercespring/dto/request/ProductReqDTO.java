package com.nashtech.ecommercespring.dto.request;

import com.nashtech.ecommercespring.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductReqDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 15, fraction = 2)
    private BigDecimal price;

    @Min(value = 0)
    private int stock;

    @NotNull
    private ProductStatus status;

    private boolean isFeatured;

    private UUID categoryId;
}
