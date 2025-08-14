

// Response DTO
package com.codegym.projectmodule5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostRegistrationResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String idCardNumber;
    private String address;
    private String city;
    private String country;
    private String reason;
    private String propertyDescription;
    private String status;
    private String adminNotes;
    private String reviewedBy;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}