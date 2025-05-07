package com.nashtech.ecommercespring.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface CachedUserDetailsService {
    UserDetails getUserDetailsByEmail(String email);
}
