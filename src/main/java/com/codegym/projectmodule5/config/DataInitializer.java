package com.codegym.projectmodule5.config;

import com.codegym.projectmodule5.entity.Role;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("Starting data initialization...");
        log.info("========================================");

        initializeRoles();
        initializeAdminUser();
        initializeHostUser();
        initializeRegularUser();

        log.info("========================================");
        log.info("Data initialization completed!");
        log.info("Test accounts created:");
        log.info("1. Admin - username: admin, password: admin123");
        log.info("2. Host  - username: host, password: host123");
        log.info("3. User  - username: user, password: user123");
        log.info("========================================");
    }

    private void initializeRoles() {
        log.info("Initializing roles...");
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleRepository.findByName(roleEnum).isEmpty()) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
                log.info("✓ Created role: {}", roleEnum.name());
            } else {
                log.info("✓ Role already exists: {}", roleEnum.name());
            }
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@rental.com")
                    .phone("0123456789")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build();

            userRepository.save(admin);
            log.info("✓ Created ADMIN user - username: admin, password: admin123");
        } else {
            log.info("✓ Admin user already exists");
        }
    }

    private void initializeHostUser() {
        if (userRepository.findByUsername("host").isEmpty()) {
            Role hostRole = roleRepository.findByName(RoleEnum.ROLE_HOST)
                    .orElseThrow(() -> new RuntimeException("Host role not found"));

            User host = User.builder()
                    .username("host")
                    .email("host@rental.com")
                    .phone("0987654321")
                    .password(passwordEncoder.encode("host123"))
                    .role(hostRole)
                    .build();

            userRepository.save(host);
            log.info("✓ Created HOST user - username: host, password: host123");
        } else {
            log.info("✓ Host user already exists");
        }
    }

    private void initializeRegularUser() {
        if (userRepository.findByUsername("user").isEmpty()) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            User regularUser = User.builder()
                    .username("user")
                    .email("user@rental.com")
                    .phone("0111222333")
                    .password(passwordEncoder.encode("user123"))
                    .role(userRole)
                    .build();

            userRepository.save(regularUser);
            log.info("✓ Created USER - username: user, password: user123");
        } else {
            log.info("✓ Regular user already exists");
        }

        // Create additional test user
        if (userRepository.findByUsername("john").isEmpty()) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            User john = User.builder()
                    .username("john")
                    .email("john@example.com")
                    .phone("0555666777")
                    .password(passwordEncoder.encode("john123"))
                    .role(userRole)
                    .build();

            userRepository.save(john);
            log.info("✓ Created additional USER - username: john, password: john123");
        }
    }
}