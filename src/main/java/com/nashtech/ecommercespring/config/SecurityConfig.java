package com.nashtech.ecommercespring.config;

import com.nashtech.ecommercespring.enums.RoleName;
import com.nashtech.ecommercespring.security.JwtAuthenticationEntryPoint;
import com.nashtech.ecommercespring.security.JwtAuthenticationFilter;
import com.nashtech.ecommercespring.security.SecurityConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAuthenticationFilter authenticationFilter;

    /*
     * Password encoder bean (uses BCrypt hashing)
     * Critical for secure password storage
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SecurityConstants.PUBLIC_API).permitAll()

                        .requestMatchers(HttpMethod.GET, SecurityConstants.ADMIN_WRITE_API).permitAll()

                        .requestMatchers(HttpMethod.GET, SecurityConstants.RATING_API).permitAll()

                        .requestMatchers(SecurityConstants.USER_API).hasAnyAuthority(
                                RoleName.ROLE_ADMIN.name(),
                                RoleName.ROLE_USER.name()
                        )

                        .requestMatchers(SecurityConstants.ADMIN_API).hasAuthority(RoleName.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.POST, SecurityConstants.ADMIN_WRITE_API).hasAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, SecurityConstants.ADMIN_WRITE_API).hasAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, SecurityConstants.ADMIN_WRITE_API).hasAuthority(RoleName.ROLE_ADMIN.name())

                        .requestMatchers(HttpMethod.POST, SecurityConstants.RATING_API).hasAnyAuthority(
                                    RoleName.ROLE_ADMIN.name(),
                                    RoleName.ROLE_USER.name()
                            )

                        .anyRequest().authenticated()
                ).httpBasic(Customizer.withDefaults());

        http.exceptionHandling( exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint));

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info().title("My API").version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
