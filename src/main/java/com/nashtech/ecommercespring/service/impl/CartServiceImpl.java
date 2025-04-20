package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.CartItemReqDTO;
import com.nashtech.ecommercespring.dto.response.CartDTO;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.CartMapper;
import com.nashtech.ecommercespring.model.Cart;
import com.nashtech.ecommercespring.model.CartItem;
import com.nashtech.ecommercespring.model.Product;
import com.nashtech.ecommercespring.model.User;
import com.nashtech.ecommercespring.repository.CartRepository;
import com.nashtech.ecommercespring.repository.ProductRepository;
import com.nashtech.ecommercespring.repository.UserRepository;
import com.nashtech.ecommercespring.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;


    @Override
    public CartDTO getCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, userId))
                );
        return cartMapper.toDto(cart);
    }

    @Override
    public CartDTO addItemToCart(CartItemReqDTO reqDTO) {
        Product product = productRepository.findById(reqDTO.getProductId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Product"))
                );

//        If a user doesn't have a cart then create it
        Cart cart = cartRepository.findByUserId(reqDTO.getUserId()).orElseGet(() -> {
                    User user = userRepository.findById(reqDTO.getUserId())
                            .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.NOT_FOUND, "User")));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

//        Update cartItem by one if it already exists
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(reqDTO.getProductId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cart.getCartItems().add(cartItem);
        }

        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public void removeItemFromCart(CartItemReqDTO reqDTO) {
        Cart cart = cartRepository.findByUserId(reqDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Cart"))
                );

        boolean removed = cart.getCartItems()
                .removeIf(item -> item.getProduct().getId().equals(reqDTO.getProductId()));

        if (!removed) {
            throw new NotFoundException(String.format(ExceptionMessages.NOT_FOUND, "Product"));
        }

        cartRepository.save(cart);
    }

    @Override
    public void deleteCart(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Cart"))
                );

        cartRepository.delete(cart);
    }

    @Override
    public CartDTO updateQuantity(CartItemReqDTO reqDTO) {
        if (reqDTO.getQuantity() <= 0) {
            removeItemFromCart(reqDTO);
            return getCart(reqDTO.getUserId());
        }

        Cart cart = cartRepository.findByUserId(reqDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Cart"))
                );

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(reqDTO.getProductId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.NOT_FOUND, "Cart Item")));

        cartItem.setQuantity(reqDTO.getQuantity());
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }
}
