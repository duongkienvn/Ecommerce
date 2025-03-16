package com.project.shopapp.config;

import com.project.shopapp.authentication.CustomAccessDeniedHandler;
import com.project.shopapp.authentication.CustomJwtDecoder;
import com.project.shopapp.authentication.CustomAuthenticationEntryPoint;
import com.project.shopapp.authentication.UserRequestAuthorizationManager;
import com.project.shopapp.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    private final CustomJwtDecoder customJwtDecoder;
    private final UserRequestAuthorizationManager userRequestAuthorizationManager;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String categoryPath = apiPrefix + "/categories/**";
        String productPath = apiPrefix + "/products/**";
        String orderPath = apiPrefix + "/orders/**";
        String orderDetailPath = apiPrefix + "/order_details/**";
        String userPath = apiPrefix + "/users/**";
        String commentPath = apiPrefix + "/comments";
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        String.format("%s/users/register", apiPrefix),
                                        String.format("%s/users/login", apiPrefix),
                                        String.format("%s/auth/logout", apiPrefix),
                                        String.format("%s/auth/refresh", apiPrefix),
                                        String.format("%s/products", apiPrefix),
                                        String.format("%s/categories", apiPrefix),
                                        String.format("%s/products/search", apiPrefix))
                                .permitAll()
                                .requestMatchers(GET, commentPath + "/users/**").hasRole(RoleEntity.ADMIN)
                                .requestMatchers(GET,
                                        categoryPath, productPath)
                                .hasAnyRole(RoleEntity.ADMIN, RoleEntity.USER)
                                .requestMatchers(GET,
                                        String.format("%s/orders/users/**", apiPrefix),
                                        userPath)
                                .access(this.userRequestAuthorizationManager)
                                .requestMatchers(PATCH, userPath).access(this.userRequestAuthorizationManager)
                                .requestMatchers(POST,
                                        categoryPath, productPath).hasRole(RoleEntity.ADMIN)
                                .requestMatchers(POST,
                                        orderPath, orderDetailPath).hasRole(RoleEntity.USER)
                                .requestMatchers(PUT,
                                        categoryPath, productPath)
                                .hasRole(RoleEntity.ADMIN)
                                .requestMatchers(PUT, userPath)
                                .access(this.userRequestAuthorizationManager)
                                .requestMatchers(DELETE,
                                        categoryPath, productPath, orderPath, orderDetailPath, commentPath + "/bulk-delete")
                                .hasRole(RoleEntity.ADMIN)
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oath2 -> oath2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .exceptionHandling(ex -> ex.accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
