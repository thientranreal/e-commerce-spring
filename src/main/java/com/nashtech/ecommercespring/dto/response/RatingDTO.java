package com.nashtech.ecommercespring.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RatingDTO {
    private UUID id;

    private int ratingValue;

    private String comment;

    private String email;

    private LocalDateTime createdOn;
}
