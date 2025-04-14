package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserCreateDTO;
import com.nashtech.ecommercespring.dto.request.UserUpdateDTO;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    JwtAuthResponse login(AuthRequest request);
    UserSignUpResDTO signUp(UserSignUpDTO userSignUpDTO);
    UserDTO createUser(UserCreateDTO userCreateDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(UUID id);
    UserDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO);
    UserDTO deleteUser(UUID id);
}
