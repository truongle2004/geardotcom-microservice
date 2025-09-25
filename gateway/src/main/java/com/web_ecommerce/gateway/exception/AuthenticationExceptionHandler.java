package com.web_ecommerce.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

public class AuthenticationExceptionHandler implements ServerAuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);
    private final ObjectMapper objectMapper;

    public AuthenticationExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        // Log details about the authentication failure
        String userId = exchange.getRequest().getHeaders().getFirst("X-USER-ID");
        String path = exchange.getRequest().getURI().getPath();
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        logger.error("Authentication failed for request. Path: {}, User-ID: {}, Authorization Header: {}, Reason: {}",
                path,
                userId != null ? userId : "Unknown",
                authHeader != null ? "[Present]" : "[Missing]",
                ex.getCause().getMessage());

        DataBuffer buffer;
        try {
            buffer = response.bufferFactory().wrap(
                    objectMapper.writeValueAsString(
                            ProblemDetailsBuilder.statusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage())
                                    .title("Unauthorized")
                                    .type(URI.create("about:blank"))
                                    .instance(URI.create(path))
                                    .build()
                    ).getBytes(StandardCharsets.UTF_8)
            );
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize error response for path: {}. Error: {}", path, e.getMessage());
            throw new RuntimeException(e);
        }

        return response.writeWith(Mono.just(buffer));
    }
}
