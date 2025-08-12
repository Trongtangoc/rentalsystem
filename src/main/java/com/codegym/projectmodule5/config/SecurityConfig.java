//package com.codegym.projectmodule5.config;
//
//import com.codegym.projectmodule5.security.jwt.JwtAuthFilter;
//import com.codegym.projectmodule5.security.AuthEntryPointJwt;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final JwtAuthFilter jwtAuthFilter;
//    private final AuthEntryPointJwt authEntryPointJwt;
//    private final CorsConfigurationSource corsConfigurationSource;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPointJwt))
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers("/", "/home").permitAll()
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/test-connection").permitAll()
//                        .requestMatchers("/debug/**").permitAll()
//                        .requestMatchers("/error").permitAll()
//
//                        // Static resources
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
//
//                        // Public house browsing
//                        .requestMatchers("/api/houses", "/api/houses/**").permitAll()
//                        .requestMatchers("/house-detail").permitAll()
//
//                        // Admin endpoints
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // Host endpoints
//                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")
//
//                        // User endpoints
//                        .requestMatchers("/user/**").authenticated()
//                        .requestMatchers("/api/users/**").authenticated()
//                        .requestMatchers("/api/bookings/**").authenticated()
//                        .requestMatchers("/api/reviews/**").authenticated()
//
//                        // All other requests
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}

package com.codegym.projectmodule5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Tạm thời cho phép tất cả
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}