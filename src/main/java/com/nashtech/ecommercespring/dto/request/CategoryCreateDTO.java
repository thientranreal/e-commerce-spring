package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreateDTO {
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 2000)
    private String description;
}
