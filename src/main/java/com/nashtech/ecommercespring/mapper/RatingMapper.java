package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.RatingReqDTO;
import com.nashtech.ecommercespring.dto.response.RatingDTO;
import com.nashtech.ecommercespring.model.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "email", source = "user.email")
    RatingDTO toDto (Rating rating);

    Rating toEntity (RatingReqDTO ratingReqDTO);

    List<RatingDTO> toRatingDTOs(List<Rating> ratings);
}
