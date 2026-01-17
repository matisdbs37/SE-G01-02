package com.unizg.fer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

import com.unizg.fer.security.rbac.JwtRoleChecker;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
/**
 * @author Martin NERON Bean in charge of the security configuration of the API
 */
public class SecurityConfig {

        private final String baseUrl = "http://localhost:8080";

        private final String[] PUBLIC_END_POINTS = {
                        "/api/v2/actuator/health",
                        "/api/v2/error",
                        "/api/v2/logout",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/bus/v3/api-docs/**",
                        "/v3/api-docs/**"
        };

        @Bean
        public JwtRoleChecker jwtRoleChecker() {
                return new JwtRoleChecker();
        }

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
                                .oauth2ResourceServer((oauth2) -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtRoleChecker())));

                return http.build();
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
        }

        /**
         * Bean used to configure ADMIN > USER roles hierarchy
         * 
         * @return RoleHierarchy
         */
        @Bean
        public RoleHierarchy roleHierarchy() {
                return RoleHierarchyImpl.withDefaultRolePrefix()
                                .role("ADMIN").implies("USER")
                                .build();
        }

        /**
         * Expression handler for custom role hierarchy
         * 
         * @param roleHierarchy
         * @return
         */
        @Bean
        public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
                DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
                expressionHandler.setRoleHierarchy(roleHierarchy);
                return expressionHandler;
        }
}