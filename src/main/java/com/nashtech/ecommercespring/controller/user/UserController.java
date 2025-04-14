package com.nashtech.ecommercespring.controller.user;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;


@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final RestClient.Builder builder;

    @PostMapping("/login")
    @Operation(summary = "Login into the system")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@RequestBody @Valid AuthRequest request){
        ApiResponse<JwtAuthResponse> response = ApiResponse.<JwtAuthResponse>builder()
                .success(true)
                .message("Login successfully")
                .data(userService.login(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up for user")
    public ResponseEntity<ApiResponse<UserSignUpResDTO>> signUp(@RequestBody @Valid UserSignUpDTO userSignUpDTO) {
        ApiResponse<UserSignUpResDTO> response = ApiResponse.<UserSignUpResDTO>builder()
                .success(true)
                .message("Sign up successfully")
                .data(userService.signUp(userSignUpDTO))
                .build();

        return ResponseEntity.ok(response);
    }
}

