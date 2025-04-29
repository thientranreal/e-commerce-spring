package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.response.CartDTO;
import com.nashtech.ecommercespring.dto.response.CartItemDTO;
import com.nashtech.ecommercespring.model.Cart;
import com.nashtech.ecommercespring.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        ProductImageMapper.class
})
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    CartDTO toDto(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "productImages", source = "product.productImages")
    CartItemDTO toDto(CartItem cartItem);

    List<CartItemDTO> toCartItemDTOs(List<CartItem> cartItems);
}
