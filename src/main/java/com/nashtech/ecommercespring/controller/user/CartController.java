package com.nashtech.ecommercespring.controller.user;

import com.nashtech.ecommercespring.dto.request.CartItemReqDTO;
import com.nashtech.ecommercespring.dto.response.CartDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Cart APIs")
public class CartController {
    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get cart by user ID")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestParam UUID userId) {
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.GET_BY_ID_SUCCESS, userId))
                .data(cartService.getCart(userId))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Add a product to cart")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(@RequestBody @Valid CartItemReqDTO reqDTO) {
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.UPDATE_SUCCESS, "Cart"))
                .data(cartService.addItemToCart(reqDTO))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(summary = "Update quantity of a product in cart")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(@RequestBody @Valid CartItemReqDTO reqDTO) {
        ApiResponse<CartDTO> response = ApiResponse.<CartDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.UPDATE_SUCCESS, "Cart"))
                .data(cartService.updateQuantity(reqDTO))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Remove a product from cart")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(@RequestBody @Valid CartItemReqDTO reqDTO) {
        cartService.removeItemFromCart(reqDTO);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(String.format(SuccessMessages.DELETE_SUCCESS, "Cart item"))
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cart")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@PathVariable UUID id) {
        cartService.deleteCart(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(String.format(SuccessMessages.DELETE_SUCCESS, "Cart"))
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
