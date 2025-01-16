package com.project.shopapp.config;

import com.project.shopapp.authentication.CustomJwtDecoder;
import com.project.shopapp.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final CustomJwtDecoder customJwtDecoder;

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
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        String.format("%s/users/register", apiPrefix),
                                        String.format("%s/users/login", apiPrefix),
                                        String.format("%s/auth/logout", apiPrefix),
                                        String.format("%s/auth/refresh", apiPrefix))
                                .permitAll()
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
                .formLogin(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oath2 -> oath2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())))
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
