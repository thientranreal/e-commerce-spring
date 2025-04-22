package com.nashtech.ecommercespring.mapper;

import com.nashtech.ecommercespring.dto.request.RoleReqDTO;
import com.nashtech.ecommercespring.dto.response.RoleDTO;
import com.nashtech.ecommercespring.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDto(Role role);

    Role toEntity(RoleReqDTO  roleReqDTO);
}
