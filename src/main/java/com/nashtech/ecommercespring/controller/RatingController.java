package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.RatingReqDTO;
import com.nashtech.ecommercespring.dto.response.RatingDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ratings")
@Tag(name = "Rating", description = "Rating APIs")
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    @Operation(summary = "Get rating by product Id")
    public ResponseEntity<ApiResponse<Page<RatingDTO>>> getRatingsByProductId(
            @RequestParam UUID productId,
            @PageableDefault(sort = "createdOn", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ApiResponse<Page<RatingDTO>> response = ApiResponse.<Page<RatingDTO>>builder()
                .success(true)
                .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "ratings"))
                .data(ratingService.getRatingsByProduct(productId, pageable))
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    @Operation(summary = "Create rating for product")
    public ResponseEntity<ApiResponse<RatingDTO>> createRating(
            @RequestBody @Valid RatingReqDTO reqDTO
    ) {
        RatingDTO ratingDTO = ratingService.createRating(reqDTO);

        ApiResponse<RatingDTO> response = ApiResponse.<RatingDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.CREATE_SUCCESS, ratingDTO.getId()))
                .data(ratingDTO)
                .build();

        return ResponseEntity.ok(response);
    }
}
