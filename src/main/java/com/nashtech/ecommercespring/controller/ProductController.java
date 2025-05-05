package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.ProductReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.ProductImageService;
import com.nashtech.ecommercespring.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    @Operation(summary = "Get all products or filter")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) boolean featured,
            @RequestParam(required = false, defaultValue = "false") boolean deleted,
            @PageableDefault(sort = "name") Pageable pageable
    ) {
        Page<ProductDTO> products;

        if (name != null && minPrice != null && maxPrice != null) {
            products = productService.getProductsByFilters(name, categoryId, minPrice, maxPrice, deleted, pageable);
        } else if (featured) {
            products = productService.getFeaturedProducts(pageable);
        } else {
            products = productService.getAllProducts(pageable);
        }

        ApiResponse<Page<ProductDTO>> response = ApiResponse.<Page<ProductDTO>>builder()
                .success(true)
                .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "products"))
                .data(products)
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

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody @Valid ProductReqDTO productReqDTO) {
        ApiResponse<ProductDTO> response = ApiResponse.<ProductDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.CREATE_SUCCESS, productReqDTO.getName()))
                .data(productService.createProduct(productReqDTO))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductReqDTO reqDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, reqDTO);

        ApiResponse<ProductDTO> response = ApiResponse.<ProductDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.UPDATE_SUCCESS, updatedProduct.getName()))
                .data(updatedProduct)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(String.format(SuccessMessages.DELETE_SUCCESS, id))
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    // ---- Product Image Management ----

    @PostMapping("/{productId}/images")
    @Operation(summary = "Add image to product")
    public ResponseEntity<ApiResponse<ProductImageDTO>> addImageToProduct(
            @PathVariable UUID productId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                ApiResponse.<ProductImageDTO>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.CREATE_SUCCESS, "Image"))
                        .data(productImageService.addImageToProduct(productId, file))
                        .build()
        );
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "Delete product image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable UUID imageId) {
        productImageService.deleteImage(imageId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.DELETE_SUCCESS, imageId))
                        .build()
        );
    }

    // ---- Product Image ----

    @GetMapping("/{productId}/images")
    @Operation(summary = "Get all images by productId")
    public ResponseEntity<ApiResponse<?>> getImages(@PathVariable UUID productId) {
        List<ProductImageDTO> images = productImageService.getImagesByProductId(productId);

        return ResponseEntity.ok(
                ApiResponse.<List<ProductImageDTO>>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "product images"))
                        .data(images)
                        .build()
        );
    }
}
