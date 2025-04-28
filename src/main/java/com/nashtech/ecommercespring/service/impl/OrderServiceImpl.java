package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.enums.OrderStatus;
import com.nashtech.ecommercespring.enums.ProductStatus;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.OrderMapper;
import com.nashtech.ecommercespring.model.*;
import com.nashtech.ecommercespring.repository.CartRepository;
import com.nashtech.ecommercespring.repository.OrderRepository;
import com.nashtech.ecommercespring.repository.UserRepository;
import com.nashtech.ecommercespring.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderDTO placeOrder(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "User"))
                );

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Cart"))
                );

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.IS_EMPTY, "Cart")
            );
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        // Check stock and convert to order items
        Order finalOrder = order;
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    int requestedQuantity = cartItem.getQuantity();

                    if (product.getStock() < requestedQuantity) {
                        throw new BadRequestException(
                                String.format(
                                        ExceptionMessages.INSUFFICIENT_STOCK,
                                        product.getStock(),
                                        product.getName()
                                )
                        );
                    }

                    // Reduce stock
                    product.setStock(product.getStock() - requestedQuantity);

                    if (product.getStock() == 0) {
                        product.setStatus(ProductStatus.OUT_OF_STOCK);
                    }

                    OrderItem orderItem = orderMapper.toOrderItem(cartItem);
                    orderItem.setOrder(finalOrder);
                    return orderItem;
                })
                .toList();

        order.setOrderItems(orderItems);

//        Calculate total price
        order.setTotal(orderItems.stream()
                .map(item -> item
                        .getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Save order and clear cart
        order = orderRepository.save(order);
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDTO getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Order")
                ));

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDTO> getOrdersByUser(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(orderMapper::toDto)
                .toList();
    }
}
