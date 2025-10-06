package com.web_ecommerce.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakReactiveJwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakReactiveJwtConverter.class);
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        try {
            // Log JWT details
            logger.debug("Processing JWT. Subject: {}, Issuer: {}, Expiration: {}, Claims: {}",
                    jwt.getSubject(), jwt.getIssuer(), jwt.getExpiresAt(), jwt.getClaims());

            // Extract realm_access claim
            Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);
            Collection<GrantedAuthority> authorities = Collections.emptyList();

            if (realmAccess != null) {
                logger.debug("Found realm_access claim: {}", realmAccess);
                Object rolesObj = realmAccess.get(ROLES_CLAIM);

                if (rolesObj instanceof Collection) {
                    Collection<String> roles = (Collection<String>) rolesObj;
                    authorities = roles.stream()
                            .map(role -> {
                                String authority = "ROLE_" + role;
                                logger.debug("Mapping role '{}' to authority '{}'", role, authority);
                                return new SimpleGrantedAuthority(authority);
                            })
                            .collect(Collectors.toList());
                } else {
                    logger.warn("Roles claim is missing or not a collection in realm_access: {}", rolesObj);
                }
            } else {
                logger.warn("No realm_access claim found in JWT for user: {}", jwt.getSubject());
            }

            logger.debug("Extracted authorities for user {}: {}", jwt.getSubject(), authorities);
            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        } catch (Exception e) {
            logger.error("Failed to process JWT for user: {}. Error: {}",
                    jwt.getSubject(), e.getMessage(), e);
            return Mono.error(new InvalidBearerTokenException("JWT processing failed: " + e.getMessage(), e));
        }
    }
}
