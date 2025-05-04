package com.nashtech.ecommercespring.dto.response;

import com.nashtech.ecommercespring.enums.RoleName;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RoleDTO {
    private UUID id;
    private RoleName roleName;
}
