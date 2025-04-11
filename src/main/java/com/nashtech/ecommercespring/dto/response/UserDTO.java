package com.nashtech.ecommercespring.dto.response;

import com.nashtech.ecommercespring.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    private String address;

    private Role role;
}
