package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.ProductReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    ProductDTO createProduct(ProductReqDTO productDTO);
    ProductDTO getProductById(UUID id);
    Page<ProductDTO> getAllProducts(Pageable pageable);
    Page<ProductDTO> getProductsByCategory(UUID categoryId, Pageable pageable);
    Page<ProductDTO> getFeaturedProducts(Pageable pageable);
    ProductDTO updateProduct(UUID id, ProductReqDTO productDTO);
    void deleteProduct(UUID id);
    Page<ProductDTO> getProductsByFilters(
            String name,
            UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);

}
