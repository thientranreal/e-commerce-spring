package com.nashtech.ecommercespring.controller.user;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User APIs")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login into the system")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(
            @RequestBody @Valid AuthRequest request,
            HttpServletResponse httpServletResponse
    ){
        ApiResponse<JwtAuthResponse> response = ApiResponse.<JwtAuthResponse>builder()
                .success(true)
                .message(SuccessMessages.LOGIN_SUCCESS_MESSAGE)
                .data(userService.login(request, httpServletResponse))
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse httpServletResponse) {
        userService.logout(httpServletResponse);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(SuccessMessages.LOGOUT_SUCCESS_MESSAGE)
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        ApiResponse<UserDTO> response = ApiResponse.<UserDTO>builder()
                .success(true)
                .message(SuccessMessages.GET_CURRENT_USER_SUCCESS)
                .data(userService.getCurrentUser(userDetails))
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

