package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.CartItemReqDTO;
import com.nashtech.ecommercespring.dto.response.CartDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void getCart_ShouldReturnCartResponse_WhenValidUserIdGiven() {
        // Arrange
        UUID userId = UUID.randomUUID();

        CartDTO mockCart = new CartDTO();
        when(cartService.getCart(userId)).thenReturn(mockCart);

        // Act
        ResponseEntity<ApiResponse<CartDTO>> response = cartController.getCart(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockCart, response.getBody().getData());
        assertEquals(
                String.format(SuccessMessages.GET_BY_ID_SUCCESS, userId),
                response.getBody().getMessage()
        );

        // Verify service call
        verify(cartService).getCart(userId);
    }

    @Test
    void addItemToCart_ShouldReturnUpdatedCart_WhenValidRequestGiven() {
        // Arrange
        CartItemReqDTO request = new CartItemReqDTO();
        CartDTO mockCart = new CartDTO();
        when(cartService.addItemToCart(request)).thenReturn(mockCart);

        // Act
        ResponseEntity<ApiResponse<CartDTO>> response = cartController.addItemToCart(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockCart, response.getBody().getData());
        assertEquals(
                String.format(SuccessMessages.UPDATE_SUCCESS, "Cart"),
                response.getBody().getMessage()
        );

        // Verify the service method was called with the correct request
        verify(cartService).addItemToCart(request);
    }

    @Test
    void updateQuantity_ShouldReturnUpdatedCart_WhenValidRequestGiven() {
        // Arrange
        CartItemReqDTO request = new CartItemReqDTO();
        CartDTO mockCart = new CartDTO();
        when(cartService.updateQuantity(request)).thenReturn(mockCart);

        // Act
        ResponseEntity<ApiResponse<CartDTO>> response = cartController.updateQuantity(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockCart, response.getBody().getData());
        assertEquals(
                String.format(SuccessMessages.UPDATE_SUCCESS, "Cart"),
                response.getBody().getMessage()
        );

        // Verify the service method was called with the correct request
        verify(cartService).updateQuantity(request);
    }

    @Test
    void removeCartItem_WhenValidRequestGiven() {
        // Arrange
        CartItemReqDTO request = new CartItemReqDTO();

        // Act
        ResponseEntity<ApiResponse<Void>> response = cartController.removeItemFromCart(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                String.format(SuccessMessages.DELETE_SUCCESS, "Cart item"),
                response.getBody().getMessage()
        );

        // Verify the service method was called with the correct request
        verify(cartService).removeItemFromCart(request);
    }

    @Test
    void deleteCart_WhenValidRequestGiven() {
        // Arrange
        UUID cartId = UUID.randomUUID();

        // Act
        ResponseEntity<ApiResponse<Void>> response = cartController.deleteCart(cartId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(
                String.format(SuccessMessages.DELETE_SUCCESS, "Cart"),
                response.getBody().getMessage()
        );

        // Verify the service method was called with the correct request
        verify(cartService).deleteCart(cartId);
    }
}
