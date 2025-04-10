package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.AuthRequest;
import com.nashtech.ecommercespring.dto.UserDTO;
import com.nashtech.ecommercespring.dto.UserSignUpDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    String login(AuthRequest request);
    UserSignUpDTO signUp(UserSignUpDTO userSignUpDTO);
    UserDTO createUser(UserDTO user);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(UUID id, UserDTO userDTO);
    void deleteUser(UUID id);
}
