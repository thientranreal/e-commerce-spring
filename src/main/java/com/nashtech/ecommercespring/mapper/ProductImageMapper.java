package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.ProductImageReqDTO;
import com.nashtech.ecommercespring.dto.response.ProductImageDTO;
import com.nashtech.ecommercespring.model.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageDTO toDto(ProductImage productImage);

    ProductImage toEntity(ProductImageReqDTO dto);
}
