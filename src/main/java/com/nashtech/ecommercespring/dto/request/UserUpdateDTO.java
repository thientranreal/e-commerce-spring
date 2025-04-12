package com.nashtech.ecommercespring.dto.request;

import com.nashtech.ecommercespring.enums.RoleName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private String address;

    private RoleName roleName;
}
