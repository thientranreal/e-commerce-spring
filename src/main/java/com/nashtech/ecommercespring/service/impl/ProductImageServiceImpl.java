package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.ProductImageMapper;
import com.nashtech.ecommercespring.model.Product;
import com.nashtech.ecommercespring.model.ProductImage;
import com.nashtech.ecommercespring.repository.ProductImageRepository;
import com.nashtech.ecommercespring.repository.ProductRepository;
import com.nashtech.ecommercespring.service.CloudinaryService;
import com.nashtech.ecommercespring.service.ProductImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    private final ProductRepository productRepository;

    private final ProductImageMapper productImageMapper;

    private final CloudinaryService cloudinaryService;

    @Override
    public ProductImageDTO addImageToProduct(UUID productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, productId))
                );

        // Upload the image to Cloudinary and get the URL
        String imageUrl = cloudinaryService.uploadImage(file);

        // Create a new ProductImage entity and associate it with the product
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);

//        Save the productImage
        return productImageMapper.toDto(productImageRepository.save(productImage));
    }

    @Override
    public List<ProductImageDTO> getImagesByProductId(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream()
                .map(productImageMapper::toDto)
                .toList();
    }

    @Override
    public void deleteImage(UUID imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, imageId))
                );

        // Delete the image from Cloudinary
        cloudinaryService.deleteImage(productImage.getImageUrl());

        // Delete the image record from the database
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
