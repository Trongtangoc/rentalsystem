package com.codegym.projectmodule5.entity;

import com.codegym.projectmodule5.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// ----- User -----
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String phone;

    // >>> Thêm field này <<<
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified; // primitive => default false
    // To this:
    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private boolean is_email_verified = false;
    @Column(name = "enabled", nullable = false)
    @Builder.Default  // This ensures the default value is used with @Builder
    private boolean enabled = true;  // Default to true for new users
    @Column(name = "account_expired", nullable = false)
    @Builder.Default
    private boolean accountExpired = false;

    @Column(name = "account_locked", nullable = false)
    @Builder.Default
    private boolean accountLocked = false;

    @Column(name = "credentials_expired", nullable = false)
    @Builder.Default
    private boolean credentialsExpired = false;

    // Add this new field
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
//    @PrePersist
//    void prePersist() {
//        // đảm bảo luôn có giá trị khi insert
//        if (!this.emailVerified) this.emailVerified = false;
//    }
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "owner")
    private List<House> houses;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "receiver")
    private List<Notification> notifications;
}