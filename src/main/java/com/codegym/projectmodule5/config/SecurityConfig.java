//// Updated SecurityConfig.java
package com.codegym.projectmodule5.config;

import com.codegym.projectmodule5.security.jwt.JwtAuthFilter;
import com.codegym.projectmodule5.security.AuthEntryPointJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPointJwt))
                .authorizeHttpRequests(auth -> auth
                        // COMPLETELY PUBLIC - No authentication required
                        .requestMatchers("/", "/home").permitAll()
                        .requestMatchers("/auth/**").permitAll()  // All auth pages
                        .requestMatchers("/api/auth/**").permitAll()  // All auth API endpoints
                        .requestMatchers("/test-connection").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Public house browsing
                        .requestMatchers("/api/houses").permitAll()
                        .requestMatchers("/api/houses/{id}").permitAll()
                        .requestMatchers("/api/houses/search").permitAll()
                        .requestMatchers("/house-detail").permitAll()
                        .requestMatchers("/api/reviews/house/**").permitAll()

                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/all").hasRole("ADMIN")
                        .requestMatchers("/api/users/{userId}").hasRole("ADMIN")
                        .requestMatchers("/api/users/{userId}/promote-to-host").hasRole("ADMIN")

                        // Host and Admin endpoints
                        .requestMatchers("/api/houses/my-houses").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers("/api/bookings/my-houses/bookings").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")

                        // Authenticated user endpoints
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
