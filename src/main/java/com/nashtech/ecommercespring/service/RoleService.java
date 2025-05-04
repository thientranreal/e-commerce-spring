package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.RoleReqDTO;
import com.nashtech.ecommercespring.dto.response.RoleDTO;

import java.util.List;

public interface RoleService {
    RoleDTO createRole(RoleReqDTO roleReqDTO);
    List<RoleDTO> getAllRoles();
}
