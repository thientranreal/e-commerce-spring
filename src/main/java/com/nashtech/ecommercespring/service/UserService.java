package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.AuthRequest;

public interface UserService {
    String login(AuthRequest request);
}
