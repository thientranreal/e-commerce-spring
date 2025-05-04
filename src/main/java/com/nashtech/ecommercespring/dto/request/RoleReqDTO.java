package com.nashtech.ecommercespring.dto.request;

import com.nashtech.ecommercespring.enums.RoleName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleReqDTO {
    private RoleName roleName;
}
