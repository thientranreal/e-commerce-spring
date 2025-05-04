package com.nashtech.ecommercespring.dto.response;

import com.nashtech.ecommercespring.model.Role;
import com.nashtech.ecommercespring.model.UserInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserSignUpResDTO {
    private UUID id;

    private String email;

    private List<UserInfo> userInfos;

    private Set<Role> roles;
}
