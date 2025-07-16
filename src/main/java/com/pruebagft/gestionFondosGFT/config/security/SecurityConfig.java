package com.pruebagft.gestionFondosGFT.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // For @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
@EnableMethodSecurity // Enables method-level security (e.g., @PreAuthorize)
public class SecurityConfig {

    // Define the security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for REST APIs, as JWT is stateless
                .authorizeHttpRequests(authorize -> authorize
                        // Allow unauthenticated access to Swagger UI and API docs
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        // Allow unauthenticated access to client creation (if public signup is desired)
                        // If client creation should be protected, remove .permitAll() and apply specific role
                        .requestMatchers("/api/clients").permitAll() // Example: Allow client creation without auth
                        .requestMatchers("/api/funds").permitAll()   // Example: Allow viewing funds without auth
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())) // Configure JWT authentication converter
                );
        return http.build();
    }

    // Configures how authorities (roles) are extracted from the JWT token
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // Configure where to find roles in the JWT token.
        // For Cognito, roles/groups are typically in the 'cognito:groups' claim.
        grantedAuthoritiesConverter.setAuthoritiesClaimName("cognito:groups");
        // Prefix for roles. Spring Security expects "ROLE_" prefix by default.
        // If your Cognito groups are like "ADMIN", they'll become "ROLE_ADMIN".
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}