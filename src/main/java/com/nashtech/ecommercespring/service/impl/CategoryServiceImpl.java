package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.CategoryReqDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.CategoryMapper;
import com.nashtech.ecommercespring.model.Category;
import com.nashtech.ecommercespring.repository.CategoryRepository;
import com.nashtech.ecommercespring.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO createCategory(CategoryReqDTO categoryDTO) {
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new BadRequestException(String.format(
                    ExceptionMessages.ALREADY_EXISTS, categoryDTO.getName())
            );
        }

        Category category = categoryMapper.toEntity(categoryDTO);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        return categoryMapper.toDto(category);
    }

    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        return categoryRepository
                .findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    public CategoryDTO updateCategory(UUID id, CategoryReqDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        categoryMapper.updateCategoryFromDto(categoryDTO, category);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        categoryRepository.delete(category);
    }
}
