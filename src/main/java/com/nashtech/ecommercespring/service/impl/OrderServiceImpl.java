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
    public OrderDTO placeOrder(UUID userId, List<UUID> productIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "User"))
                );

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Cart"))
                );

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> productIds.contains(item.getProduct().getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.IS_EMPTY, "Cart")
            );
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        // Check stock and convert to order items
        List<OrderItem> orderItems = processCartItems(selectedItems, order);
        order.setOrderItems(orderItems);

//        Calculate total price
        order.setTotal(calculateTotal(orderItems));

        // Save order and clear cart
        order = orderRepository.save(order);

        clearCart(cart, productIds);

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

    @Transactional
    @Override
    public OrderDTO updateStatusById(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Order")
                ));

//        Restore stock when order is cancelled
        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            restoreProductStock(order);
        }

        order.setStatus(status);

        return orderMapper.toDto(orderRepository.save(order));
    }

    // ====================== Helper Methods ======================

    private List<OrderItem> processCartItems(List<CartItem> selectedItems, Order order) {
        return selectedItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();

                    validateProductAvailability(product, cartItem.getQuantity());

                    updateProductStock(product, cartItem.getQuantity());

                    OrderItem orderItem = orderMapper.toOrderItem(cartItem);
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();
    }

    private void validateProductAvailability(Product product, int requestedQty) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException(
                    String.format(
                            ExceptionMessages.PRODUCT_STATUS_IS,
                            product.getName(),
                            product.getStatus()
                    )
            );
        }

        if (product.getStock() < requestedQty) {
            throw new BadRequestException(
                    String.format(
                            ExceptionMessages.INSUFFICIENT_STOCK,
                            product.getStock(),
                            product.getName()
                    )
            );
        }
    }

    private void updateProductStock(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        if (product.getStock() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }
    }

    private void clearCart(Cart cart, List<UUID> productIds) {
        cart.getCartItems().removeIf(item -> productIds.contains(item.getProduct().getId()));
        cartRepository.save(cart);
    }

    private BigDecimal calculateTotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item
                        .getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void restoreProductStock(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());

            if (product.getStatus() == ProductStatus.OUT_OF_STOCK && product.getStock() > 0) {
                product.setStatus(ProductStatus.ACTIVE);
            }
        });
    }
}
