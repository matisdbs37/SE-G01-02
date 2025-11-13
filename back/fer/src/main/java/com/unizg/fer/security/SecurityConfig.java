package com.unizg.fer.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
/**
 * @author Martin NERON Bean in charge of the security configuration of the API
 */
public class SecurityConfig {

    //Authorized public end points 
    private final static String OAUTH2_MATCHER = "/oauth2/**";
    private final static String LOGIN_MATCHER = "/login/**";

    private final String[] allowedOrigins = {"http://localhost:3000", "http://localhost:5173"};

    @Bean
    /**
     * This bean intercept any http/https request and revoke non authenticated
     * except for the authorized endpoints
     */
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // no need for rest API
                .authorizeHttpRequests((authz) -> authz // auth config
                .requestMatchers(LOGIN_MATCHER, OAUTH2_MATCHER).permitAll()
                .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/user/me", true)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));// check google jwt signature and get jwt as authentication
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
