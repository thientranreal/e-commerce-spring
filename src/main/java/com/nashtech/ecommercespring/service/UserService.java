package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserReqDTO;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    JwtAuthResponse login(AuthRequest request);
    UserSignUpResDTO signUp(UserSignUpDTO userSignUpDTO);
    UserDTO createUser(UserReqDTO userCreateDTO);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(UUID id);
    UserDTO updateUser(UUID id, UserReqDTO userReqDTO);
    void deleteUser(UUID id);
}
