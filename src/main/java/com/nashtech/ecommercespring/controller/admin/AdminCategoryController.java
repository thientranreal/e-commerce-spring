package com.nashtech.ecommercespring.controller.admin;

import com.nashtech.ecommercespring.dto.request.CategoryReqDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin Category", description = "Admin Category management APIs")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryDTO>> create(@RequestBody CategoryReqDTO dto) {
        ApiResponse<CategoryDTO> response = ApiResponse.<CategoryDTO>builder()
                .success(true)
                .message("Create a new category successfully")
                .data(categoryService.createCategory(dto))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories() {
        ApiResponse<List<CategoryDTO>> response = ApiResponse.<List<CategoryDTO>>builder()
                .success(true)
                .message("Get all categories successfully")
                .data(categoryService.getAllCategories())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable UUID id) {
        ApiResponse<CategoryDTO> response = ApiResponse.<CategoryDTO>builder()
                .success(true)
                .message("Get category by ID successfully")
                .data(categoryService.getCategoryById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable UUID id,
            @RequestBody @Valid CategoryReqDTO reqDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, reqDTO);

        ApiResponse<CategoryDTO> response = ApiResponse.<CategoryDTO>builder()
                .success(true)
                .message("Update category successfully")
                .data(updatedCategory)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Delete category successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
