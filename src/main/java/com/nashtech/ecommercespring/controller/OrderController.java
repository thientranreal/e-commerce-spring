package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.OrderReqDTO;
import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "Order APIs")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place an order for a user")
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(@RequestBody @Valid OrderReqDTO orderReqDTO) {
        return ResponseEntity.ok(
                ApiResponse.<OrderDTO>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.CREATE_SUCCESS, "Order"))
                        .data(orderService.placeOrder(orderReqDTO.getUserId(), orderReqDTO.getProductIds()))
                        .build()
        );
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(
                ApiResponse.<OrderDTO>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.GET_BY_ID_SUCCESS, orderId))
                        .data(orderService.getOrderById(orderId))
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get all orders for a user")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByUser(@RequestParam UUID userId) {
        return ResponseEntity.ok(
                ApiResponse.<List<OrderDTO>>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "user orders"))
                        .data(orderService.getOrdersByUser(userId))
                        .build()
        );
    }
}
