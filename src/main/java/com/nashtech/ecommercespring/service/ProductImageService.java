package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.ProductImageReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductImageDTO;

import java.util.List;
import java.util.UUID;

public interface ProductImageService {
    void addImageToProduct(UUID productId, ProductImageReqDTO imageDTO);

    List<ProductImageDTO> getImagesByProductId(UUID productId);

    void deleteImage(UUID imageId);

    ProductImageDTO getImageById(UUID imageId);
}
