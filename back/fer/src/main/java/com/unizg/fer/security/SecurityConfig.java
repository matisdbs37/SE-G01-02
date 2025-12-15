package com.unizg.fer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
/**
 * @author Martin NERON Bean in charge of the security configuration of the API
 */
public class SecurityConfig {

    private final String baseUrl = "http://localhost:8080";

    private final String[] PUBLIC_END_POINTS = {
            "/api/v2/actuator/health",
            "/api/v2/error",
            "/api/v2/logout"
    };

    @Bean
    /**
     * This bean intercept any http/https request and revoke non authenticated
     * except for the authorized endpoints
     */
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
                
        http

        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((authorized) -> authorized.requestMatchers(PUBLIC_END_POINTS)
                                                             .permitAll()
                                                             .anyRequest()
                                                             .authenticated())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
    }
}