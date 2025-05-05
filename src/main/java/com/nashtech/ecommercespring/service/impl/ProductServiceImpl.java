package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.ProductReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.enums.ProductStatus;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.ProductMapper;
import com.nashtech.ecommercespring.model.Category;
import com.nashtech.ecommercespring.model.Product;
import com.nashtech.ecommercespring.repository.CategoryRepository;
import com.nashtech.ecommercespring.repository.ProductRepository;
import com.nashtech.ecommercespring.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductReqDTO productDTO) {
        Category category =  categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(String
                        .format(ExceptionMessages.NOT_FOUND, productDTO.getCategoryId()))
                );

        Product product = productMapper.toEntity(productDTO);
        product.setCategory(category);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        return productMapper.toDto(product);
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findByDeletedFalse(pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getFeaturedProducts(Pageable pageable) {
        return productRepository.findByIsFeaturedTrueAndDeletedFalse(pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ProductDTO> getProductsByFilters(
            String name,
            UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            boolean deleted,
            Pageable pageable) {
        return productRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            predicates.add(criteriaBuilder.between(root.get("price"), minPrice, maxPrice));

            if (!deleted) {
                predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(productMapper::toDto);
    }

    @Override
    public ProductDTO updateProduct(UUID id, ProductReqDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        Category category =  categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException(String
                        .format(ExceptionMessages.NOT_FOUND, productDTO.getCategoryId()))
                );

        productMapper.updateProductFromDto(productDTO, product);
        product.setCategory(category);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        product.setDeleted(true);
        product.setStatus(ProductStatus.DISCONTINUED);
        productRepository.save(product);
    }
}
