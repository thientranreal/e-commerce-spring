package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.ProductImageReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.ProductImageMapper;
import com.nashtech.ecommercespring.model.Product;
import com.nashtech.ecommercespring.model.ProductImage;
import com.nashtech.ecommercespring.repository.ProductImageRepository;
import com.nashtech.ecommercespring.repository.ProductRepository;
import com.nashtech.ecommercespring.service.ProductImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    private final ProductRepository productRepository;

    private final ProductImageMapper productImageMapper;

    @Override
    public void addImageToProduct(UUID productId, ProductImageReqDTO imageDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, productId))
                );

        ProductImage productImage = productImageMapper.toEntity(imageDTO);
        productImage.setProduct(product);
        productImageRepository.save(productImage);
    }

    @Override
    public List<ProductImageDTO> getImagesByProductId(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream()
                .map(productImageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(UUID imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, imageId))
                );

        productImageRepository.delete(productImage);
    }

    @Override
    public ProductImageDTO getImageById(UUID imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, imageId))
                );

        return productImageMapper.toDto(productImage);
    }
}
