package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.CategoryCreateDTO;
import com.nashtech.ecommercespring.dto.request.CategoryUpdateDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
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
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryCreateDTO dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable UUID id,
            @RequestBody @Valid CategoryUpdateDTO categoryUpdateDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryUpdateDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
