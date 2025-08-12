// DataInitializer.java
package com.codegym.projectmodule5.config;

import com.codegym.projectmodule5.entity.Role;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing data...");
        initializeRoles();
        initializeAdminUser();
        System.out.println("Data initialization completed!");
    }

    private void initializeRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleRepository.findByName(roleEnum).isEmpty()) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
                System.out.println("Created role: " + roleEnum.name());
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
            System.out.println("Created admin user - username: admin, password: admin123");
        }
    }
}