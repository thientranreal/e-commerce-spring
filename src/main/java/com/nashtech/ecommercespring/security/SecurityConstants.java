package com.nashtech.ecommercespring.security;

public class SecurityConstants {
    public static final String[] PUBLIC_API = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/v*/users/login",
            "/api/v*/users/signup",
            "/api/v*/users/logout",
            "/api/v*/vnpay/**"
    };

    public static final String[] ADMIN_API = {
            "/api/v*/roles/**",
            "/api/v*/users/**",
    };

    public static final String[] USER_API = {
            "/api/v*/cart/**",
            "/api/v*/orders/**",
            "/api/v*/users/me"
    };

    public static final String RATING_API = "/api/v*/ratings/**";

    public static final String[] ADMIN_WRITE_API = {
            "/api/v*/categories/**",
            "/api/v*/products/**"
    };
}
