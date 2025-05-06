package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.RatingReqDTO;
import com.nashtech.ecommercespring.dto.response.RatingDTO;
import com.nashtech.ecommercespring.enums.OrderStatus;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.RatingMapper;
import com.nashtech.ecommercespring.model.Product;
import com.nashtech.ecommercespring.model.Rating;
import com.nashtech.ecommercespring.model.User;
import com.nashtech.ecommercespring.repository.OrderRepository;
import com.nashtech.ecommercespring.repository.ProductRepository;
import com.nashtech.ecommercespring.repository.RatingRepository;
import com.nashtech.ecommercespring.repository.UserRepository;
import com.nashtech.ecommercespring.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void getRatingsByProduct_ShouldReturnPagedRatingDTOs_WhenProductExists() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 2);

        Product product = new Product();
        product.setId(productId);

        Rating rating1 = new Rating();
        rating1.setId(UUID.randomUUID());

        Rating rating2 = new Rating();
        rating2.setId(UUID.randomUUID());

        RatingDTO dto1 = new RatingDTO();
        dto1.setId(rating1.getId());

        RatingDTO dto2 = new RatingDTO();
        dto2.setId(rating2.getId());

        List<Rating> ratings = List.of(rating1, rating2);
        Page<Rating> ratingPage = new PageImpl<>(ratings, pageable, ratings.size());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(ratingRepository.findByProductId(productId, pageable)).thenReturn(ratingPage);
        when(ratingMapper.toDto(rating1)).thenReturn(dto1);
        when(ratingMapper.toDto(rating2)).thenReturn(dto2);

        // Act
        Page<RatingDTO> result = ratingService.getRatingsByProduct(productId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(dto1.getId(), result.getContent().get(0).getId());
        assertEquals(dto2.getId(), result.getContent().get(1).getId());

        verify(productRepository).findById(productId);
        verify(ratingRepository).findByProductId(productId, pageable);
        verify(ratingMapper).toDto(rating1);
        verify(ratingMapper).toDto(rating2);
    }

    @Test
    void getRatingsByProduct_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Arrange
        UUID productId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 2);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> ratingService.getRatingsByProduct(productId, pageable)
        );

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, productId), ex.getMessage());
        verify(productRepository).findById(productId);
        verifyNoInteractions(ratingRepository, ratingMapper);
    }

    @Test
    void createRating_ShouldCreateRating_WhenAllConditionsAreMet() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        RatingReqDTO reqDTO = new RatingReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setProductId(productId);
        reqDTO.setRatingValue(5);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Book");
        product.setAvgRating(4.0);
        product.setRatingCount(2);

        User user = new User();
        user.setId(userId);

        Rating rating = new Rating();
        rating.setId(UUID.randomUUID());
        rating.setProduct(product);
        rating.setUser(user);
        rating.setRatingValue(5);

        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setId(rating.getId());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
                userId, productId, OrderStatus.CONFIRMED)).thenReturn(true);
        when(ratingMapper.toEntity(reqDTO)).thenReturn(rating);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toDto(rating)).thenReturn(ratingDTO);

        // Act
        RatingDTO result = ratingService.createRating(reqDTO);

        // Assert
        assertNotNull(result);
        assertEquals(rating.getId(), result.getId());
        assertEquals(3, product.getRatingCount());
        assertEquals((4.0 * 2 + 5) / 3, product.getAvgRating());

        verify(productRepository).save(product);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toDto(rating);
    }

    @Test
    void createRating_ShouldThrowNotFoundException_WhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        RatingReqDTO reqDTO = new RatingReqDTO();
        reqDTO.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> ratingService.createRating(reqDTO)
        );

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, productId), ex.getMessage());
        verifyNoInteractions(userRepository, orderRepository, ratingMapper, ratingRepository);
    }

    @Test
    void createRating_ShouldThrowNotFoundException_WhenUserNotFound() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RatingReqDTO reqDTO = new RatingReqDTO();
        reqDTO.setProductId(productId);
        reqDTO.setUserId(userId);

        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> ratingService.createRating(reqDTO)
        );

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, userId), ex.getMessage());
        verify(productRepository).findById(productId);
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(orderRepository, ratingMapper, ratingRepository);
    }

    @Test
    void createRating_ShouldThrowBadRequest_WhenUserHasNotPurchasedProduct() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RatingReqDTO reqDTO = new RatingReqDTO();
        reqDTO.setProductId(productId);
        reqDTO.setUserId(userId);

        Product product = new Product();
        product.setId(productId);
        product.setName("Unpurchased Product");

        User user = new User();
        user.setId(userId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
                userId, productId, OrderStatus.CONFIRMED)).thenReturn(false);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> ratingService.createRating(reqDTO)
        );

        assertEquals(String.format(ExceptionMessages.PRODUCT_NOT_PURCHASED, product.getName()), ex.getMessage());

        verify(productRepository).findById(productId);
        verify(userRepository).findById(userId);
        verify(orderRepository).existsByUserIdAndOrderItemsProductIdAndStatus(
                userId, productId, OrderStatus.CONFIRMED);
        verifyNoMoreInteractions(ratingRepository, ratingMapper);
    }
}
