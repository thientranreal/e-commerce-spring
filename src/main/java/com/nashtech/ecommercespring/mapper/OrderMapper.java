package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.dto.response.OrderItemDTO;
import com.nashtech.ecommercespring.model.CartItem;
import com.nashtech.ecommercespring.model.Order;
import com.nashtech.ecommercespring.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDTO toDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDTO toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "product.price")
    OrderItem toOrderItem(CartItem cartItem);

    List<OrderItemDTO> toOrderItemDTOs(List<OrderItem> cartItems);
}
