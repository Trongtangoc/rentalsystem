package com.codegym.projectmodule5.config;

import com.codegym.projectmodule5.entity.*;
import com.codegym.projectmodule5.enums.*;
import com.codegym.projectmodule5.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final ImageRepository imageRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("Starting data initialization...");
        log.info("========================================");

        initializeRoles();
        User admin = initializeAdminUser();
        User host = initializeHostUser();
        User regularUser = initializeRegularUser();
        User john = initializeJohnUser();

        // Create sample houses for the host
        List<House> houses = initializeHouses(host);

        // Create sample bookings
        initializeBookings(houses, regularUser, john);

        // Create sample reviews
        initializeReviews(houses, regularUser, john);

        log.info("========================================");
        log.info("Data initialization completed!");
        log.info("Test accounts created:");
        log.info("1. Admin - username: admin, password: admin123");
        log.info("2. Host  - username: host, password: host123");
        log.info("3. User  - username: user, password: user123");
        log.info("4. User  - username: john, password: john123");
        log.info("Sample houses, bookings, and reviews created!");
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
            }
        }
    }

    private User initializeAdminUser() {
        var existing = userRepository.findByUsername("admin");
        if (existing.isPresent()) {
            log.info("✓ Admin user already exists");
            return existing.get();
        }

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
        return admin;
    }

    private User initializeHostUser() {
        var existing = userRepository.findByUsername("host");
        if (existing.isPresent()) {
            log.info("✓ Host user already exists");
            return existing.get();
        }

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
        return host;
    }

    private User initializeRegularUser() {
        var existing = userRepository.findByUsername("user");
        if (existing.isPresent()) {
            log.info("✓ Regular user already exists");
            return existing.get();
        }

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
        return regularUser;
    }

    private User initializeJohnUser() {
        var existing = userRepository.findByUsername("john");
        if (existing.isPresent()) {
            log.info("✓ John user already exists");
            return existing.get();
        }

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
        return john;
    }

    private List<House> initializeHouses(User host) {
        List<House> houses = new ArrayList<>();

        // Check if houses already exist
        if (!houseRepository.findAllByOwnerId(host.getId()).isEmpty()) {
            log.info("✓ Houses already exist for host");
            return houseRepository.findAllByOwnerId(host.getId());
        }

        // House 1: Beach Villa
        House beachVilla = House.builder()
                .title("Luxury Beach Villa with Ocean")
                .description("Beautiful 3-bedroom villa right on the beach. Perfect for families and groups. "
                        + "Features include private pool, fully equipped kitchen, BBQ area, and direct beach access. "
                        + "Wake up to stunning ocean views and fall asleep to the sound of waves.")
                .price(250.0)
                .address("123 Beach Road, Miami, FL 33139")
                .status(HouseStatus.AVAILABLE)
                .owner(host)
                .build();
        beachVilla = houseRepository.save(beachVilla);
        houses.add(beachVilla);

        // Add images for beach villa
        List<Image> beachImages = List.of(
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(beachVilla).build(),
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(beachVilla).build(),
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(beachVilla).build()
        );
        imageRepository.saveAll(beachImages);

        // House 2: Mountain Cabin
        House mountainCabin = House.builder()
                .title("Cozy Mountain Cabin Retreat")
                .description("Rustic 2-bedroom cabin in the mountains. Perfect for a quiet getaway. "
                        + "Features fireplace, hot tub, hiking trails nearby, and spectacular mountain views. "
                        + "Fully furnished with modern amenities while maintaining rustic charm.")
                .price(150.0)
                .address("456 Mountain Trail, Aspen, CO 81611")
                .status(HouseStatus.AVAILABLE)
                .owner(host)
                .build();
        mountainCabin = houseRepository.save(mountainCabin);
        houses.add(mountainCabin);

        // Add images for mountain cabin
        List<Image> cabinImages = List.of(
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(mountainCabin).build(),
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(mountainCabin).build()
        );
        imageRepository.saveAll(cabinImages);

//        // House 3: City Apartment
//        House cityApartment = House.builder()
//                .title("Modern Downtown Apartment")
//                .description("Stylish 1-bedroom apartment in the heart of the city. Walking distance to restaurants, "
//                        + "shopping, and entertainment. Features modern kitchen, workspace, high-speed wifi, "
//                        + "and city skyline views from the balcony.")
//                .price(120.0)
//                .address("789 Downtown Plaza, New York, NY 10001")
//                .status(HouseStatus.AVAILABLE)
//                .owner(host)
//                .build();
//        cityApartment = houseRepository.save(cityApartment);
//        houses.add(cityApartment);
//
//        // Add images for city apartment
//        List<Image> apartmentImages = List.of(
//                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
//                ).house(cityApartment).build(),
//                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
//                ).house(cityApartment).build()
//        );
//        imageRepository.saveAll(apartmentImages);

        // House 4: Mountain Cabin
        House mountainCabin4 = House.builder()
                .title("Cozy Mountain Cabin Retreat")
                .description("Rustic 2-bedroom cabin in the mountains. Perfect for a quiet getaway. "
                        + "Features fireplace, hot tub, hiking trails nearby, and spectacular mountain views. "
                        + "Fully furnished with modern amenities while maintaining rustic charm.")
                .price(150.0)
                .address("456 Mountain Trail, Aspen, CO 81611")
                .status(HouseStatus.AVAILABLE)
                .owner(host)
                .build();
        mountainCabin4 = houseRepository.save(mountainCabin4);
        houses.add(mountainCabin4);

        // Add images for mountain cabin
        List<Image> cabinImages4 = List.of(
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(mountainCabin4).build(),
                Image.builder().url("/uploads/images/imgHouse/5a2df9d8-5597-4182-baba-017f8995414f.avif"
                ).house(mountainCabin4).build()
        );
        imageRepository.saveAll(cabinImages4);

        log.info("✓ Created {} sample houses for host", houses.size());
        return houses;

    }


    private void initializeBookings(List<House> houses, User user1, User user2) {
        // Check if bookings already exist
        if (!bookingRepository.findAll().isEmpty()) {
            log.info("✓ Bookings already exist");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Booking 1: user books beach villa (upcoming)
        Booking booking1 = Booking.builder()
                .startDate(now.plusDays(10))
                .endDate(now.plusDays(15))
                .status(BookingStatus.CONFIRMED)
                .house(houses.get(0)) // Beach Villa
                .user(user1)
                .build();
        bookingRepository.save(booking1);

        // Booking 2: john books mountain cabin (upcoming)
        Booking booking2 = Booking.builder()
                .startDate(now.plusDays(20))
                .endDate(now.plusDays(23))
                .status(BookingStatus.PENDING)
                .house(houses.get(1)) // Mountain Cabin
                .user(user2)
                .build();
        bookingRepository.save(booking2);

        // Booking 3: user books city apartment (past)
        Booking booking3 = Booking.builder()
                .startDate(now.minusDays(30))
                .endDate(now.minusDays(27))
                .status(BookingStatus.DONE)
                .house(houses.get(2)) // City Apartment
                .user(user1)
                .build();
        bookingRepository.save(booking3);

        log.info("✓ Created sample bookings");
    }

    private void initializeReviews(List<House> houses, User user1, User user2) {
        // Check if reviews already exist
        if (!reviewRepository.findAll().isEmpty()) {
            log.info("✓ Reviews already exist");
            return;
        }

        // Review 1: user reviews city apartment (from past booking)
        Review review1 = Review.builder()
                .rating(5)
                .comment("Amazing apartment! Perfect location, very clean and modern. "
                        + "The host was very responsive and helpful. Would definitely stay again!")
                .user(user1)
                .house(houses.get(2)) // City Apartment
                .build();
        reviewRepository.save(review1);

        // Review 2: john reviews beach villa
        Review review2 = Review.builder()
                .rating(4)
                .comment("Great villa with beautiful ocean views. The private pool was fantastic. "
                        + "Only minor issue was the WiFi was a bit slow. Overall great experience!")
                .user(user2)
                .house(houses.get(0)) // Beach Villa
                .build();
        reviewRepository.save(review2);

        // Review 3: another review for beach villa
        Review review3 = Review.builder()
                .rating(5)
                .comment("Perfect vacation home! Everything was exactly as described. "
                        + "The beach access was amazing and the kids loved the pool.")
                .user(user1)
                .house(houses.get(0)) // Beach Villa
                .build();
        reviewRepository.save(review3);

        log.info("✓ Created sample reviews");
    }
}