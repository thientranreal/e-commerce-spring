package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.RoleReqDTO;
import com.nashtech.ecommercespring.dto.response.RoleDTO;
import com.nashtech.ecommercespring.mapper.RoleMapper;
import com.nashtech.ecommercespring.model.Role;
import com.nashtech.ecommercespring.repository.RoleRepository;
import com.nashtech.ecommercespring.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Override
    public RoleDTO createRole(RoleReqDTO roleReqDTO) {
        Role role = roleMapper.toEntity(roleReqDTO);

        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .toList();
    }
}
