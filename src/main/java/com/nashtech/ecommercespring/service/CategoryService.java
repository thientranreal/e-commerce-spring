package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.CategoryReqDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    CategoryDTO createCategory(CategoryReqDTO categoryDTO);
    CategoryDTO getCategoryById(UUID id);
    Page<CategoryDTO> getAllCategories(Pageable pageable);
    Page<CategoryDTO> getCategoryByName(String name, Pageable pageable);
    CategoryDTO updateCategory(UUID id, CategoryReqDTO categoryDTO);
    void deleteCategory(UUID id);
}
