package com.project.shopapp.config;

import com.project.shopapp.entity.RoleEntity;
import com.project.shopapp.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtFilter jwtFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String categoryPath = apiPrefix + "/categories/**";
        String productPath = apiPrefix + "/products/**";
        String orderPath = apiPrefix + "/orders/**";
        String orderDetailPath = apiPrefix + "/order_details/**";

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        String.format("%s/users/register", apiPrefix),
                                        String.format("%s/users/login", apiPrefix)).permitAll()
                                .requestMatchers(GET,
                                        categoryPath, productPath, orderPath, orderDetailPath)
                                .hasAnyRole(RoleEntity.ADMIN, RoleEntity.USER)
                                .requestMatchers(POST,
                                        categoryPath, productPath).hasRole(RoleEntity.ADMIN)
                                .requestMatchers(POST,
                                        orderPath, orderDetailPath).hasRole(RoleEntity.USER)
                                .requestMatchers(PUT,
                                        categoryPath, productPath, orderPath, orderDetailPath)
                                .hasRole(RoleEntity.ADMIN)
                                .requestMatchers(DELETE,
                                        categoryPath, productPath, orderPath, orderDetailPath)
                                .hasRole(RoleEntity.ADMIN)
                                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
