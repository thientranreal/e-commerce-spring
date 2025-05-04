package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.RoleReqDTO;
import com.nashtech.ecommercespring.dto.response.RoleDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Admin Role", description = "Admin Role management APIs")
public class RoleController {

    private RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        ApiResponse<List<RoleDTO>> response = ApiResponse.<List<RoleDTO>>builder()
                .success(true)
                .message(String.format(SuccessMessages.GET_ALL_SUCCESS, "roles"))
                .data(roleService.getAllRoles())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create roles")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@RequestBody @Valid RoleReqDTO dto) {
        ApiResponse<RoleDTO> response = ApiResponse.<RoleDTO>builder()
                .success(true)
                .message(String.format(SuccessMessages.CREATE_SUCCESS, dto.getRoleName()))
                .data(roleService.createRole(dto))
                .build();

        return ResponseEntity.ok(response);
    }
}
