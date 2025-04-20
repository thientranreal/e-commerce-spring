package com.nashtech.ecommercespring.controller.user;

import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
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
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Get all products or filter by category")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAllProducts(
            @RequestParam(required = false) UUID categoryId,
            @PageableDefault(sort = "name") Pageable pageable
    ) {
        Page<ProductDTO> products = (categoryId == null)
                ? productService.getAllProducts(pageable)
                : productService.getProductsByCategory(categoryId, pageable);

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

    // ---- Product Image ----

    @GetMapping("/images")
    @Operation(summary = "Get all images by productId or imageId")
    public ResponseEntity<ApiResponse<?>> getImages(
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID imageId
    ) {
        if (imageId != null) {
            ProductImageDTO image = productImageService.getImageById(imageId);

            return ResponseEntity.ok(
                    ApiResponse.<ProductImageDTO>builder()
                            .success(true)
                            .message(String.format(SuccessMessages.GET_BY_ID_SUCCESS, imageId))
                            .data(image)
                            .build()
            );
        }

        if (productId != null) {
            List<ProductImageDTO> images = productImageService.getImagesByProductId(productId);

            return ResponseEntity.ok(
                    ApiResponse.<List<ProductImageDTO>>builder()
                            .success(true)
                            .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "product images"))
                            .data(images)
                            .build()
            );
        }

        throw new BadRequestException(
                String.format(ExceptionMessages.NOT_FOUND, "productId or imageId parameters")
        );
    }
}
