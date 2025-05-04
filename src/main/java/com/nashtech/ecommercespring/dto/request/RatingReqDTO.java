package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RatingReqDTO {
    @Min(1)
    @Max(5)
    private int ratingValue;

    @Size(max = 1024)
    private String comment;

    @NotNull
    private UUID userId;

    @NotNull
    private UUID productId;
}
