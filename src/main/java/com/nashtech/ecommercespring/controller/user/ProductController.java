package com.nashtech.ecommercespring.controller.user;

import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.ProductImageService;
import com.nashtech.ecommercespring.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product APIs")
public class ProductController {
    private final ProductImageService productImageService;

    private final ProductService productService;

    // ---- Product ----

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAllProducts(
            @PageableDefault(sort = "name") Pageable pageable
    ) {
        ApiResponse<Page<ProductDTO>> response = ApiResponse.<Page<ProductDTO>>builder()
                .success(true)
                .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "products"))
                .data(productService.getAllProducts(pageable))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
        ApiResponse<ProductDTO> response = ApiResponse.<ProductDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.GET_BY_ID_SUCCESS, id))
                .data(productService.getProductById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get product by category")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getProductsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(sort = "name") Pageable pageable
    ) {
        Page<ProductDTO> products = productService.getProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ProductDTO>>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "products by category"))
                        .data(products)
                        .build()
        );
    }

    // ---- Product Image ----

    @GetMapping("/{productId}/images")
    @Operation(summary = "Get all images for a product")
    public ResponseEntity<ApiResponse<List<ProductImageDTO>>> getImagesByProductId(@PathVariable UUID productId) {
        List<ProductImageDTO> images = productImageService.getImagesByProductId(productId);

        return ResponseEntity.ok(
                ApiResponse.<List<ProductImageDTO>>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "product images"))
                        .data(images)
                        .build()
        );
    }

    @GetMapping("/images/{imageId}")
    @Operation(summary = "Get product image by ID")
    public ResponseEntity<ApiResponse<ProductImageDTO>> getImageById(@PathVariable UUID imageId) {
        ProductImageDTO image = productImageService.getImageById(imageId);

        return ResponseEntity.ok(
                ApiResponse.<ProductImageDTO>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.GET_BY_ID_SUCCESS, imageId))
                        .data(image)
                        .build()
        );
    }
}
