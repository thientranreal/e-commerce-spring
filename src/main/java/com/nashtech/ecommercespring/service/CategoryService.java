package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.CategoryReqDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDTO createCategory(CategoryReqDTO categoryDTO);
    CategoryDTO getCategoryById(UUID id);
    List<CategoryDTO> getAllCategories();
    CategoryDTO updateCategory(UUID id, CategoryReqDTO categoryDTO);
    void deleteCategory(UUID id);
}
