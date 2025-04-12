package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.UserCreateDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.request.UserUpdateDTO;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User entity);

    UserSignUpResDTO toDtoForSignUp(User entity);

    User toEntity(UserCreateDTO dto);

    User toEntity(UserSignUpDTO dto);

    void updateUserFromDto(UserUpdateDTO dto, @MappingTarget User user);
}
