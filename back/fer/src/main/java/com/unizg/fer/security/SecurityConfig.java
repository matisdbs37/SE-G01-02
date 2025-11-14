package com.unizg.fer.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
/**
 * @author Martin NERON Bean in charge of the security configuration of the API
 */
public class SecurityConfig {

    // Authorized public end points
    private final static String BASE_URL = "api/auth";
    private final static String OAUTH2_MATCHER = "/oauth2/**";
    private final static String LOGIN_MATCHER = "api/auth/login/**";
    private final static String REGISTER_MATCHER = "api/auth/register/**";
    private final static String LOGOUT_MATCHER = "/logout/**";
    private final static String HOME_PAGE = "http://localhost:4200/profile/edit_profile";

    private final String[] allowedOrigins = { "http://localhost:3000", "http://localhost:5173", "http://localhost:4200",
            "https://votre-domaine.com",
           // "http://localhost:8080/oauth2/authorization/google" 
        };

    @Bean
    /**
     * This bean intercept any http/https request and revoke non authenticated
     * except for the authorized endpoints
     */
    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // no need for rest API
                .authorizeHttpRequests((authz) -> authz // auth config
                        .requestMatchers(LOGIN_MATCHER, OAUTH2_MATCHER, REGISTER_MATCHER, LOGOUT_MATCHER, "/login").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl(HOME_PAGE, true))
                .logout(logout -> logout
                        .logoutUrl("/logout") // Logout URL
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                        .invalidateHttpSession(true)
                        .deleteCookies("MINDAPI_SESSION", "JSESSIONID") // Delete session cookies
                        .clearAuthentication(true))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));// check google jwt signature and
                                                                                       // get jwt as authentication
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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(
                clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("http://localhost:8080/login?logout=true");

        return successHandler;
    }

}