package com.nashtech.ecommercespring.security;

public class SecurityConstants {
    public static final String[] PUBLIC_API = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/users/login",
            "/api/users/signup",
            "/api/users/logout",
            "/api/vnpay/**"
    };

    public static final String[] ADMIN_API = {
            "/api/roles/**",
            "/api/users/**",
    };

    public static final String[] USER_API = {
            "/api/cart/**",
            "/api/orders/**",
            "/api/users/me"
    };

    public static final String RATING_API = "/api/ratings/**";

    public static final String[] ADMIN_WRITE_API = {
            "/api/categories/**",
            "/api/products/**"
    };
}
