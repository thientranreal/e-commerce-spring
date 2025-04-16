package com.nashtech.ecommercespring.controller.admin;

import com.nashtech.ecommercespring.dto.request.ProductImageReqDTO;
import com.nashtech.ecommercespring.dto.request.ProductReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.ProductImageService;
import com.nashtech.ecommercespring.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin Product", description = "Admin Product management APIs")
public class AdminProductController {
    private final ProductService productService;

    private final ProductImageService productImageService;

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
    public ResponseEntity<ApiResponse<Void>> addImageToProduct(
            @PathVariable UUID productId,
            @RequestBody @Valid ProductImageReqDTO imageDTO) {

        productImageService.addImageToProduct(productId, imageDTO);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(String.format(SuccessMessages.CREATE_SUCCESS, "Image"))
                        .data(null)
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
}
