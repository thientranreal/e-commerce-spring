package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.CategoryReqDTO;
import com.nashtech.ecommercespring.dto.response.CategoryDTO;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.CategoryMapper;
import com.nashtech.ecommercespring.model.Category;
import com.nashtech.ecommercespring.repository.CategoryRepository;
import com.nashtech.ecommercespring.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_ShouldCreateAndReturnCategory_WhenCategoryDoesNotExist() {
        // Arrange
        CategoryReqDTO reqDTO = new CategoryReqDTO();
        reqDTO.setName("Science");

        Category category = new Category();
        category.setName("Science");

        CategoryDTO expectedDTO = new CategoryDTO();
        expectedDTO.setName("Science");

        when(categoryRepository.findByName("Science")).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(reqDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedDTO);

        // Act
        CategoryDTO result = categoryService.createCategory(reqDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.getName(), result.getName());
        verify(categoryRepository).findByName("Science");
        verify(categoryMapper).toEntity(reqDTO);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void createCategory_ShouldThrowBadRequestException_WhenCategoryAlreadyExists() {
        // Arrange
        CategoryReqDTO reqDTO = new CategoryReqDTO();
        reqDTO.setName("Science");

        when(categoryRepository.findByName("Science"))
                .thenReturn(Optional.of(new Category()));

        // Act & Assert
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> categoryService.createCategory(reqDTO)
        );

        assertEquals(String.format(
                ExceptionMessages.ALREADY_EXISTS, reqDTO.getName()
        ), ex.getMessage());

        verify(categoryRepository).findByName("Science");
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    void getCategoryById_ShouldReturnCategoryDTO_WhenCategoryExists() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Fiction");

        CategoryDTO expectedDTO = new CategoryDTO();
        expectedDTO.setId(categoryId);
        expectedDTO.setName("Fiction");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDTO);

        // Act
        CategoryDTO result = categoryService.getCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals(expectedDTO.getName(), result.getName());

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void getCategoryById_ShouldThrowNotFoundException_WhenCategoryDoesNotExist() {
        // Arrange
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> categoryService.getCategoryById(categoryId)
        );

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, categoryId), ex.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoMoreInteractions(categoryMapper);
    }

    @Test
    void getAllCategories_ShouldReturnPagedCategoryDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);

        Category category1 = new Category();
        category1.setId(UUID.randomUUID());
        category1.setName("Science");

        Category category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("History");

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setId(category1.getId());
        dto1.setName("Science");

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setId(category2.getId());
        dto2.setName("History");

        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        // Act
        Page<CategoryDTO> result = categoryService.getAllCategories(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(dto1.getName(), result.getContent().get(0).getName());
        assertEquals(dto2.getName(), result.getContent().get(1).getName());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category1);
        verify(categoryMapper).toDto(category2);
    }

    @Test
    void getCategoryByName_ShouldReturnPagedCategoryDTOs_WhenNameMatches() {
        // Arrange
        String searchName = "fic";
        Pageable pageable = PageRequest.of(0, 2);

        Category category1 = new Category();
        category1.setId(UUID.randomUUID());
        category1.setName("Fiction");

        Category category2 = new Category();
        category2.setId(UUID.randomUUID());
        category2.setName("Science Fiction");

        CategoryDTO dto1 = new CategoryDTO();
        dto1.setId(category1.getId());
        dto1.setName("Fiction");

        CategoryDTO dto2 = new CategoryDTO();
        dto2.setId(category2.getId());
        dto2.setName("Science Fiction");

        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findByNameContainingIgnoreCase(searchName, pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        // Act
        Page<CategoryDTO> result = categoryService.getCategoryByName(searchName, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(dto1.getName(), result.getContent().get(0).getName());
        assertEquals(dto2.getName(), result.getContent().get(1).getName());

        verify(categoryRepository).findByNameContainingIgnoreCase(searchName, pageable);
        verify(categoryMapper).toDto(category1);
        verify(categoryMapper).toDto(category2);
    }

    @Test
    void updateCategory_ShouldUpdateAndReturnCategoryDTO_WhenCategoryExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryReqDTO reqDTO = new CategoryReqDTO();
        reqDTO.setName("Updated Name");

        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("Old Name");

        Category updatedCategory = new Category();
        updatedCategory.setId(id);
        updatedCategory.setName("Updated Name");

        CategoryDTO expectedDTO = new CategoryDTO();
        expectedDTO.setId(id);
        expectedDTO.setName("Updated Name");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        doAnswer(invocation -> {
            existingCategory.setName(reqDTO.getName());
            return null;
        }).when(categoryMapper).updateCategoryFromDto(reqDTO, existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expectedDTO);

        // Act
        CategoryDTO result = categoryService.updateCategory(id, reqDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.getName(), result.getName());

        verify(categoryRepository).findById(id);
        verify(categoryMapper).updateCategoryFromDto(reqDTO, existingCategory);
        verify(categoryRepository).save(existingCategory);
        verify(categoryMapper).toDto(updatedCategory);
    }

    @Test
    void updateCategory_ShouldThrowNotFoundException_WhenCategoryDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryReqDTO reqDTO = new CategoryReqDTO();
        reqDTO.setName("New Name");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> categoryService.updateCategory(id, reqDTO)
        );

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, id), ex.getMessage());
        verify(categoryRepository).findById(id);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    void deleteCategory_ShouldDeleteCategory_WhenCategoryExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setId(id);
        category.setName("To Be Deleted");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(id);

        // Assert
        verify(categoryRepository).findById(id);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_ShouldThrowNotFoundException_WhenCategoryDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> categoryService.deleteCategory(id)
        );

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, id), ex.getMessage());
        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).delete(any());
    }
}
