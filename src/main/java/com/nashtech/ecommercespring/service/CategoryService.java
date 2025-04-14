package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.CategoryCreateDTO;
import com.nashtech.ecommercespring.dto.request.CategoryUpdateDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryDTO createCategory(CategoryCreateDTO categoryDTO);
    CategoryDTO getCategoryById(UUID id);
    List<CategoryDTO> getAllCategories();
    CategoryDTO updateCategory(UUID id, CategoryUpdateDTO categoryUpdateDTO);
    CategoryDTO deleteCategory(UUID id);
}
