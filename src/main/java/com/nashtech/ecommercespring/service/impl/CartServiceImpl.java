package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.CartItemReqDTO;
import com.nashtech.ecommercespring.dto.response.CartDTO;
import com.nashtech.ecommercespring.enums.ProductStatus;
import com.nashtech.ecommercespring.exception.BadRequestException;
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
        Product product = getActiveProduct(reqDTO.getProductId());

        if (product.getStock() == 0) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.INSUFFICIENT_STOCK, 0, product.getName())
            );
        }

//        If a user doesn't have a cart then create it
        Cart cart = getOrCreateCart(reqDTO.getUserId());

//        Update cartItem by one if it already exists
        CartItem cartItem = findCartItem(cart, reqDTO.getProductId());

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
        Cart cart = getCartByUserId(reqDTO.getUserId());

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

        Cart cart = getCartByUserId(reqDTO.getUserId());

        CartItem cartItem = findCartItem(cart, reqDTO.getProductId());

        if (cartItem == null) {
            throw new NotFoundException(String.format(ExceptionMessages.NOT_FOUND, "Cart Item"));
        }

        if (cartItem.getProduct().getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException(
                    String.format(
                            ExceptionMessages.PRODUCT_STATUS_IS,
                            cartItem.getProduct().getName(),
                            cartItem.getProduct().getStatus()
                    )
            );
        }

        if (cartItem.getProduct().getStock() < reqDTO.getQuantity()) {
            throw new BadRequestException(
                    String.format(
                            ExceptionMessages.INSUFFICIENT_STOCK,
                            cartItem.getProduct().getStock(),
                            cartItem.getProduct().getName()
                    )
            );
        }

        cartItem.setQuantity(reqDTO.getQuantity());
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    // ====================== Helper Methods ======================

    private Cart getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Cart")));
    }

    private Product getActiveProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, "Product"))
                );

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BadRequestException(
                    String.format(
                            ExceptionMessages.PRODUCT_STATUS_IS,
                            product.getName(),
                            product.getStatus())

            );
        }

        return product;
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format(ExceptionMessages.NOT_FOUND, "User")));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private CartItem findCartItem(Cart cart, UUID productId) {
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
