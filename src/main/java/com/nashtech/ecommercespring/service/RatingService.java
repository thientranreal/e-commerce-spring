package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.RatingReqDTO;
import com.nashtech.ecommercespring.dto.response.RatingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RatingService {
    Page<RatingDTO> getRatingsByProduct(UUID productId, Pageable pageable);
    RatingDTO createRating(RatingReqDTO ratingReqDTO);
}
