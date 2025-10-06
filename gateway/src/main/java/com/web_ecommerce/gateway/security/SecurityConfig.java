package com.web_ecommerce.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web_ecommerce.gateway.exception.AccessDeniedExceptionHandler;
import com.web_ecommerce.gateway.exception.AuthenticationExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private ObjectMapper objectMapper;
    private static final String ROLE_USER = "ROLE_USER";

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public AuthenticationExceptionHandler authenticationExceptionHandler() {
        return new AuthenticationExceptionHandler(objectMapper);
    }

    @Bean
    public AccessDeniedExceptionHandler accessDeniedExceptionHandler() {
        return new AccessDeniedExceptionHandler(objectMapper);
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                "/actuator/prometheus",
                                "/actuator/health/**",
                                "/swagger-ui",
                                "/swagger-ui/**",
                                "/error",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/sale/products/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/sale/carts/**").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/sale/carts/**").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/user/districts").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/user/wards").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/user/provinces").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/user/profile").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.PUT, "/api/v1/user/profile").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/user/address").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.PUT, "/api/v1/user/address").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.POST, "/api/v1/sale/orders/").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/payment/handle_success").hasAuthority(ROLE_USER)
                        .pathMatchers(HttpMethod.GET, "/api/v1/payment/vnpay_return/").hasAuthority(ROLE_USER)
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationExceptionHandler())
                        .accessDeniedHandler(accessDeniedExceptionHandler())
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverterForKeycloak())));

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-USER-ID"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }


    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverterForKeycloak() {
        return new KeycloakReactiveJwtConverter();
    }
}


