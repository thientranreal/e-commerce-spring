package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserReqDTO;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserService {
    JwtAuthResponse login(AuthRequest request, HttpServletResponse httpServletResponse);
    void logout(HttpServletResponse httpServletResponse);
    UserSignUpResDTO signUp(UserSignUpDTO userSignUpDTO);
    UserDTO createUser(UserReqDTO userCreateDTO);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(UUID id);
    UserDTO updateUser(UUID id, UserReqDTO userReqDTO);
    void deleteUser(UUID id);
    UserDTO getCurrentUser(UserDetails userDetails);
}
