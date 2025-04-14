package com.nashtech.ecommercespring.controller.admin;

import com.nashtech.ecommercespring.dto.request.UserCreateDTO;
import com.nashtech.ecommercespring.dto.request.UserUpdateDTO;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin User", description = "Admin User management APIs")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                .success(true)
                .message("Create a new user successfully")
                .data(userService.createUser(userCreateDTO))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        ApiResponse<List<UserDTO>> response = ApiResponse.<List<UserDTO>>builder()
                .success(true)
                .message("Get all users successfully")
                .data(userService.getAllUsers())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID id) {
        ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                .success(true)
                .message("Get user by ID successfully")
                .data(userService.getUserById(id))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO
    ) {
        ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                .success(true)
                .message("Update user successfully")
                .data(userService.updateUser(id, userUpdateDTO))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Delete user successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
