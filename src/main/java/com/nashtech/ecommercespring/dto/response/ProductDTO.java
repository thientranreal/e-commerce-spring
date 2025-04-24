package com.nashtech.ecommercespring.dto.response;

import com.nashtech.ecommercespring.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProductDTO {
    private UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private int stock;

    private ProductStatus status;

    private boolean isFeatured;

    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;
    private boolean deleted;

    private CategoryDTO category;

    private List<ProductImageDTO> productImages;

    private List<RatingDTO> ratings;
}
