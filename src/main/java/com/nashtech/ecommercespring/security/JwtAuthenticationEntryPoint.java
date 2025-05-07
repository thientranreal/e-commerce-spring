package com.nashtech.ecommercespring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        logger.error(ExceptionMessages.UNAUTHORIZED_ACCESS,
                request.getRequestURI(),
                request.getRemoteAddr(),
                authException.getMessage()
        );

        // Create the ApiResponse object
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .message(authException.getMessage())
                .data(null)
                .build();

        // Set response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Write the ApiResponse object to the response body
        response.setContentType("application/json");
        response.getWriter().write(apiResponseToJson(apiResponse));
    }


    // Utility method to convert ApiResponse to JSON string
    private String apiResponseToJson(ApiResponse<Void> apiResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(apiResponse);
        } catch (IOException e) {
            // Handle the JSON conversion error (fallback if needed)
            return "{\"success\": false, \"message\": \"Error in serialization\"}";
        }
    }
}
