package com.nashtech.ecommercespring.controller.admin;

import com.nashtech.ecommercespring.dto.request.ProductReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
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

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin Product", description = "Admin Product management APIs")
public class AdminProductController {
    private final ProductService productService;

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

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getAllProducts(
            @PageableDefault(size = 10, sort = "name") Pageable pageable
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
}
