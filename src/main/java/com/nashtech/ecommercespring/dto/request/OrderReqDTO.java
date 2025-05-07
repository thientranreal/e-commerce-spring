package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderReqDTO {
    @NotNull
    private UUID userId;

    @NotEmpty
    private List<UUID> productIds;
}
