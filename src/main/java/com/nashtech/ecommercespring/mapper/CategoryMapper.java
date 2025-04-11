package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.CategoryCreateDTO;
import com.nashtech.ecommercespring.dto.request.CategoryUpdateDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
import com.nashtech.ecommercespring.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);

    Category toEntity(CategoryCreateDTO dto);

    void updateCategoryFromDto(CategoryUpdateDTO dto, @MappingTarget Category category);
}
