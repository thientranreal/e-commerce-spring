package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.ProductReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductDTO;
import com.nashtech.ecommercespring.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDto(Product product);

    Product toEntity(ProductReqDTO dto);

    void updateProductFromDto(ProductReqDTO dto, @MappingTarget Product product);
}
