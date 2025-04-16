package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageReqDTO {
    @NotBlank
    private String imageUrl;
}
