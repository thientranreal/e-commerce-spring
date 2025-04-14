package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.CategoryCreateDTO;
import com.nashtech.ecommercespring.dto.request.CategoryUpdateDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.CategoryMapper;
import com.nashtech.ecommercespring.model.Category;
import com.nashtech.ecommercespring.repository.CategoryRepository;
import com.nashtech.ecommercespring.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO createCategory(CategoryCreateDTO categoryDTO) {
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new BadRequestException("Category with name " + categoryDTO.getName() + " already exists");
        }

        Category category = categoryMapper.toEntity(categoryDTO);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));

        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateCategory(UUID id, CategoryUpdateDTO categoryUpdateDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));

        categoryMapper.updateCategoryFromDto(categoryUpdateDTO, category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id " + id + " not found"));

        categoryRepository.delete(category);

        return categoryMapper.toDto(category);
    }
}
