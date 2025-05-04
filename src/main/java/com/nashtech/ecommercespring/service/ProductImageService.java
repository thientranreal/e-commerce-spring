package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductImageService {
    ProductImageDTO addImageToProduct(UUID productId, MultipartFile file);

    List<ProductImageDTO> getImagesByProductId(UUID productId);

    void deleteImage(UUID imageId);

    ProductImageDTO getImageById(UUID imageId);
}
