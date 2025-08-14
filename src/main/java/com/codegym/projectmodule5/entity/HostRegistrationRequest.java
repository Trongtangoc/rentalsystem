package com.codegym.projectmodule5.entity;

import com.codegym.projectmodule5.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "host_registration_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostRegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String reason; // Why they want to become a host

    @Column(columnDefinition = "TEXT")
    private String propertyDescription; // What kind of properties they plan to list

    private String idCardNumber; // Government ID

    private String phoneNumber;

    private String address;

    private String city;

    private String country;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private String adminNotes; // Admin's reason for approval/rejection

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy; // Admin who reviewed the request

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}