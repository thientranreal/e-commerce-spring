package com.nashtech.ecommercespring.security;

public class SecurityConstants {
    public static final String[] PUBLIC_API = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/**"
    };

    public static final String[] ADMIN_API = {
            "/api/admin/**"
    };
}
