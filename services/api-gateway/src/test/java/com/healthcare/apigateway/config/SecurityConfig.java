package com.healthcare.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                // preflight CORS : traité par la Gateway, sans token
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/health").permitAll()

                // patients : portail de rendez-vous
                .pathMatchers("/rest/api/doctors/**", "/rest/api/appointments/**")
                    .hasAnyRole("PATIENT", "MEDECIN", "ADMINISTRATEUR")
                .pathMatchers("/rest/api/patients/**")
                    .hasAnyRole("PATIENT", "MEDECIN", "ADMINISTRATEUR")

                // données médicales sensibles : personnel soignant uniquement
                .pathMatchers("/graphql", "/soap/**", "/telemetry/**", "/orchestrator/**")
                    .hasAnyRole("MEDECIN", "ADMINISTRATEUR")

                .pathMatchers("/actuator/**").hasRole("ADMINISTRATEUR")
                .anyExchange().authenticated())
            .oauth2ResourceServer(o -> o.jwt(j ->
                j.jwtAuthenticationConverter(keycloakConverter())));
        return http.build();
    }

    /** Transforme realm_access.roles du JWT Keycloak en ROLE_xxx pour Spring Security. */
    private ReactiveJwtAuthenticationConverterAdapter keycloakConverter() {
        Converter<Jwt, Collection<GrantedAuthority>> roles = jwt -> {
            Map<String, Object> realm = jwt.getClaim("realm_access");
            if (realm == null || !(realm.get("roles") instanceof Collection<?> list)) {
                return List.of();
            }
            return list.stream()
                    .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                    .toList();
        };
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}