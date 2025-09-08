package com.codegym.projectmodule5.config;

import com.codegym.projectmodule5.entity.*;
import com.codegym.projectmodule5.enums.HouseStatus;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("Starting data initialization...");
        log.info("========================================");

        initializeRoles();
        initializeAdminUser();
        initializeHostUser();
        initializeRegularUser();
        initializeHousesWithImages();

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
    // Thay thế method initializeHousesWithImages() đơn giản hơn
    private void initializeHousesWithImages() {
        log.info("=== INITIALIZING HOUSES ===");

        try {
            // Get host user
            User host = userRepository.findByUsername("host")
                    .orElseThrow(() -> new RuntimeException("Host user not found"));
            log.info("✓ Found host user: {}", host.getUsername());

            // Check current count
            long currentHousesCount = houseRepository.count();
            log.info("Current houses in DB: {}", currentHousesCount);

            // Define houses to create
            String[] houseTitles = {
                    "Villa sang trọng gần biển",
                    "Căn hộ hiện đại trung tâm",
                    "Nhà phố ấm cúng Hà Nội",
                    "Studio hiện đại Đà Nẵng",
                    "Penthouse cao cấp"
            };

            // Check which houses don't exist yet
            List<String> existingTitles = houseRepository.findAll().stream()
                    .map(House::getTitle)
                    .collect(Collectors.toList());

            int created = 0;

            // House 1
            if (!existingTitles.contains(houseTitles[0])) {
                House villa = House.builder()
                        .title(houseTitles[0])
                        .description("Villa 3 phòng ngủ, view biển tuyệt đẹp, đầy đủ tiện nghi cao cấp")
                        .price(2500000.0)
                        .address("123 Tran Phu, Nha Trang, Khanh Hoa")
                        .status(HouseStatus.AVAILABLE)
                        .owner(host)
                        .build();

                villa = houseRepository.save(villa);
                imageRepository.saveAll(List.of(
                        Image.builder().url("/uploads/images/pv1.jpg").house(villa).build(),
                        Image.builder().url("/uploads/images/pv1.jpg").house(villa).build()
                ));
                log.info("✓ Created: {}", villa.getTitle());
                created++;
            }

            // House 2
            if (!existingTitles.contains(houseTitles[1])) {
                House apartment = House.builder()
                        .title(houseTitles[1])
                        .description("Căn hộ 2 phòng ngủ tại trung tâm Sài Gòn, gần các khu mua sắm")
                        .price(1800000.0)
                        .address("456 Nguyen Hue, District 1, Ho Chi Minh City")
                        .status(HouseStatus.AVAILABLE)
                        .owner(host)
                        .build();

                apartment = houseRepository.save(apartment);
                imageRepository.saveAll(List.of(
                        Image.builder().url("/uploads/images/p1.avif").house(apartment).build(),
                        Image.builder().url("/uploads/images/2.webp").house(apartment).build()
                ));
                log.info("✓ Created: {}", apartment.getTitle());
                created++;
            }

            // House 3
            if (!existingTitles.contains(houseTitles[2])) {
                House townhouse = House.builder()
                        .title(houseTitles[2])
                        .description("Nhà phố 4 tầng tại Hà Nội, phù hợp cho gia đình lớn")
                        .price(2200000.0)
                        .address("789 Hoan Kiem, Ha Noi")
                        .status(HouseStatus.AVAILABLE)
                        .owner(host)
                        .build();

                townhouse = houseRepository.save(townhouse);
                imageRepository.saveAll(List.of(
                        Image.builder().url("/uploads/images/p1.avif").house(townhouse).build(),
                        Image.builder().url("/uploads/images/p1.avif").house(townhouse).build()
                ));
                log.info("✓ Created: {}", townhouse.getTitle());
                created++;
            }

            // House 4
            if (!existingTitles.contains(houseTitles[3])) {
                House studio = House.builder()
                        .title(houseTitles[3])
                        .description("Studio nhỏ gọn, tiện nghi, gần bãi biển Mỹ Khê")
                        .price(1200000.0)
                        .address("321 Bach Dang, Da Nang")
                        .status(HouseStatus.AVAILABLE)
                        .owner(host)
                        .build();

                studio = houseRepository.save(studio);
                imageRepository.saveAll(List.of(
                        Image.builder().url("/uploads/images/1.png").house(studio).build(),
                        Image.builder().url("/uploads/images/2.webp").house(studio).build()
                ));
                log.info("✓ Created: {}", studio.getTitle());
                created++;
            }

            // House 5
            if (!existingTitles.contains(houseTitles[4])) {
                House penthouse = House.builder()
                        .title(houseTitles[4])
                        .description("Penthouse tầng cao nhất, view toàn thành phố, đầy đủ tiện nghi 5 sao")
                        .price(5000000.0)
                        .address("888 Nguyen Van Linh, District 7, Ho Chi Minh City")
                        .status(HouseStatus.AVAILABLE)
                        .owner(host)
                        .build();

                penthouse = houseRepository.save(penthouse);
                imageRepository.saveAll(List.of(
                        Image.builder().url("/uploads/images/1.png").house(penthouse).build(),
                        Image.builder().url("/uploads/images/2.webp").house(penthouse).build()
                ));
                log.info("✓ Created: {}", penthouse.getTitle());
                created++;
            }

            // Final result
            long finalCount = houseRepository.count();
            log.info("🎉 Houses initialization completed!");
            log.info("   - Created: {} new houses", created);
            log.info("   - Total houses: {}", finalCount);
            log.info("   - All houses have AVAILABLE status");

        } catch (Exception e) {
            log.error("❌ Error creating houses: ", e);
            // Don't throw exception to prevent app startup failure
        }
    }
}