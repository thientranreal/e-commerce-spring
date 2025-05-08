package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.OrderReqDTO;
import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void placeOrder_shouldReturnCreatedOrder_whenRequestIsValid() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        OrderReqDTO reqDTO = new OrderReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setProductIds(productIds);

        OrderDTO mockOrder = new OrderDTO();

        when(orderService.placeOrder(reqDTO.getUserId(), reqDTO.getProductIds())).thenReturn(mockOrder);

        // Act
        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.placeOrder(reqDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockOrder, response.getBody().getData());
        assertEquals(
                String.format(SuccessMessages.CREATE_SUCCESS, "Order"),
                response.getBody().getMessage()
        );

        verify(orderService).placeOrder(userId, productIds);
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderDTO mockOrder = new OrderDTO();

        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);

        // Act
        ResponseEntity<ApiResponse<OrderDTO>> response = orderController.getOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockOrder, response.getBody().getData());
        assertEquals(
                String.format(SuccessMessages.GET_BY_ID_SUCCESS, orderId),
                response.getBody().getMessage()
        );

        verify(orderService).getOrderById(orderId);
    }

    @Test
    void getOrdersByUser_shouldReturnOrderList_whenUserHasOrders() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<OrderDTO> mockOrders = List.of(new OrderDTO(), new OrderDTO());

        when(orderService.getOrdersByUser(userId)).thenReturn(mockOrders);

        // Act
        ResponseEntity<ApiResponse<List<OrderDTO>>> response = orderController.getOrdersByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(mockOrders, response.getBody().getData());
        assertEquals(
                String.format(SuccessMessages.GET_ALL_SUCCESS, "user orders"),
                response.getBody().getMessage()
        );

        verify(orderService).getOrdersByUser(userId);
    }
}
