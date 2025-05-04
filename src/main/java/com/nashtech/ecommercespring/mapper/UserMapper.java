package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.UserReqDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User entity);

    UserSignUpResDTO toDtoForSignUp(User entity);

    User toEntity(UserReqDTO dto);

    User toEntity(UserSignUpDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserReqDTO dto, @MappingTarget User user);
}
