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

//package com.codegym.projectmodule5.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()  // Tạm thời cho phép tất cả
//                );
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}

//package com.codegym.projectmodule5.config;
//
//import com.codegym.projectmodule5.security.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authenticationProvider(authenticationProvider())
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers("/", "/home", "/auth/**", "/api/auth/**").permitAll()
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
//                        .requestMatchers("/test-connection", "/error").permitAll()
//                        .requestMatchers("/debug/**").permitAll() // Add debug endpoints
//
//                        // Admin only
//                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
//
//                        // Host and Admin
//                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")
//
//                        // Authenticated users
//                        .requestMatchers("/user/**").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/api/users/**").hasAnyRole("USER", "HOST", "ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/auth/login")
//                        .loginProcessingUrl("/auth/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/dashboard", true)
//                        .failureUrl("/auth/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/auth/logout")
//                        .logoutSuccessUrl("/auth/login?logout=true")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}

//package com.codegym.projectmodule5.config;
//
//import com.codegym.projectmodule5.security.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authenticationProvider(authenticationProvider())
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers("/", "/home", "/auth/**", "/api/auth/**").permitAll()
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
//                        .requestMatchers("/test-connection", "/error").permitAll()
//                        .requestMatchers("/debug/**").permitAll() // Add debug endpoints
//
//                        // Test endpoints (for development)
//                        .requestMatchers("/test/user-access").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/test/host-access").hasAnyRole("HOST", "ADMIN")
//                        .requestMatchers("/test/admin-access").hasRole("ADMIN")
//
//                        // Admin only
//                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
//
//                        // Host and Admin
//                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")
//
//                        // Authenticated users
//                        .requestMatchers("/user/**").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/api/users/**").hasAnyRole("USER", "HOST", "ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/auth/login")
//                        .loginProcessingUrl("/auth/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/dashboard", true)
//                        .failureUrl("/auth/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/auth/logout")
//                        .logoutSuccessUrl("/auth/login?logout=true")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}

//package com.codegym.projectmodule5.config;
//
//import com.codegym.projectmodule5.security.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authenticationProvider(authenticationProvider())
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers("/", "/home", "/auth/**", "/api/auth/**").permitAll()
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
//                        .requestMatchers("/test-connection", "/error").permitAll()
//                        .requestMatchers("/debug/**").permitAll() // Add debug endpoints
//
//                        // Test endpoints (for development)
//                        .requestMatchers("/test/user-access").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/test/host-access").hasAnyRole("HOST", "ADMIN")
//                        .requestMatchers("/test/admin-access").hasRole("ADMIN")
//
//                        // Admin only
//                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
//
//                        // Host and Admin
//                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")
//
//                        // Authenticated users
//                        .requestMatchers("/user/**").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/api/users/**").hasAnyRole("USER", "HOST", "ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/auth/login")
//                        .loginProcessingUrl("/auth/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/dashboard", true) // Force redirect to /dashboard
//                        .successForwardUrl("/dashboard") // Alternative: forward to dashboard
//                        .failureUrl("/auth/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/auth/logout")
//                        .logoutSuccessUrl("/auth/login?logout=true")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}

//package com.codegym.projectmodule5.config;
//
//import com.codegym.projectmodule5.security.CustomLoginSuccessHandler;
//import com.codegym.projectmodule5.security.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomUserDetailsService customUserDetailsService;
//    private final CustomLoginSuccessHandler customLoginSuccessHandler;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authenticationProvider(authenticationProvider())
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers("/", "/home", "/auth/**", "/api/auth/**").permitAll()
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
//                        .requestMatchers("/test-connection", "/error").permitAll()
//                        .requestMatchers("/debug/**").permitAll() // Add debug endpoints
//
//                        // Test endpoints (for development)
//                        .requestMatchers("/test/user-access").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/test/host-access").hasAnyRole("HOST", "ADMIN")
//                        .requestMatchers("/test/admin-access").hasRole("ADMIN")
//
//                        // Admin only
//                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
//
//                        // Host and Admin
//                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")
//
//                        // Authenticated users
//                        .requestMatchers("/user/**").hasAnyRole("USER", "HOST", "ADMIN")
//                        .requestMatchers("/api/users/**").hasAnyRole("USER", "HOST", "ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/auth/login")
//                        .loginProcessingUrl("/auth/login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .successHandler(customLoginSuccessHandler) // Use custom success handler
//                        .failureUrl("/auth/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/auth/logout")
//                        .logoutSuccessUrl("/auth/login?logout=true")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                );
//
//        return http.build();
//    }
//}


package com.codegym.projectmodule5.config;

import com.codegym.projectmodule5.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            log.info("=== Login Success ===");
            log.info("User: {}", authentication.getName());
            log.info("Authorities: {}", authentication.getAuthorities());

            String targetUrl = "/dashboard";

            // Determine target URL based on role
            var authorities = authentication.getAuthorities();
            for (var authority : authorities) {
                String role = authority.getAuthority();
                log.info("Checking role: {}", role);

                if (role.equals("ROLE_ADMIN")) {
                    targetUrl = "/admin/dashboard";
                    break;
                } else if (role.equals("ROLE_HOST")) {
                    targetUrl = "/host/dashboard";
                    break;
                } else if (role.equals("ROLE_USER")) {
                    targetUrl = "/user/dashboard";
                    break;
                }
            }

            log.info("Redirecting to: {}", targetUrl);
            response.sendRedirect(targetUrl);
        };
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            log.error("=== Login Failed ===");
            log.error("Error: {}", exception.getMessage());
            log.error("Username attempted: {}", request.getParameter("username"));

            request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
            response.sendRedirect("/auth/login?error=true");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Security Filter Chain");

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/", "/home").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/test-connection", "/error").permitAll()
                        .requestMatchers("/debug/**").permitAll()

                        // Authenticated endpoints
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/host/**").hasAnyRole("HOST", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // API endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login") // This is where the form posts to
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error")
                );

        return http.build();
    }
}