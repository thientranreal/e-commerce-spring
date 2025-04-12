package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserCreateDTO;
import com.nashtech.ecommercespring.dto.request.UserUpdateDTO;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login into the system")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody @Valid AuthRequest request){
        String token = userService.login(request);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up for user")
    public ResponseEntity<UserSignUpResDTO> signUp(@RequestBody @Valid UserSignUpDTO userSignUpDTO) {
        return ResponseEntity.ok(userService.signUp(userSignUpDTO));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        return ResponseEntity.ok(userService.createUser(userCreateDTO));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateDTO userUpdateDTO
    ) {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}

