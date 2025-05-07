package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.RatingReqDTO;
import com.nashtech.ecommercespring.dto.response.RatingDTO;
import com.nashtech.ecommercespring.enums.OrderStatus;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.RatingMapper;
import com.nashtech.ecommercespring.model.*;
import com.nashtech.ecommercespring.repository.OrderRepository;
import com.nashtech.ecommercespring.repository.ProductRepository;
import com.nashtech.ecommercespring.repository.RatingRepository;
import com.nashtech.ecommercespring.repository.UserRepository;
import com.nashtech.ecommercespring.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;

    private final RatingMapper ratingMapper;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    @Override
    public Page<RatingDTO> getRatingsByProduct(UUID productId, Pageable pageable) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, productId))
                );

        return ratingRepository
                .findByProductId(productId, pageable)
                .map(ratingMapper::toDto);
    }

    @Transactional
    @Override
    public RatingDTO createRating(RatingReqDTO ratingReqDTO) {
        Product product = productRepository
                .findById(ratingReqDTO.getProductId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, ratingReqDTO.getProductId())
                ));

        User user = userRepository
                .findById(ratingReqDTO.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, ratingReqDTO.getUserId())
                ));

//        Check if user bought that product
        if (!orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
                ratingReqDTO.getUserId(),
                ratingReqDTO.getProductId(),
                OrderStatus.CONFIRMED
        )) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.PRODUCT_NOT_PURCHASED, product.getName())
            );
        }

//        Calculate avg rating
        updateProductRating(product, ratingReqDTO.getRatingValue());

        Rating rating = ratingMapper.toEntity(ratingReqDTO);
        rating.setProduct(product);
        rating.setUser(user);

        return ratingMapper.toDto(ratingRepository.save(rating));
    }

//    ==================================== Helper Method===========================================

    private void updateProductRating(Product product, int newRating) {
        int currentCount = product.getRatingCount();
        double currentAvg = product.getAvgRating();

        double newAvg = ((currentAvg * currentCount) + newRating) / (currentCount + 1);

        product.setAvgRating(newAvg);
        product.setRatingCount(currentCount + 1);

        productRepository.save(product);
    }
}
