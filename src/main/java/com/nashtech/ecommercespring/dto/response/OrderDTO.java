package com.nashtech.ecommercespring.dto.response;

import com.nashtech.ecommercespring.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderDTO {
    private UUID id;
    private UUID userId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdOn;
    private List<OrderItemDTO> orderItems;
}
