package com.codegym.projectmodule5.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HostRegistrationRequestDto {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Government ID is required")
    private String idCardNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Please explain why you want to become a host")
    @Size(min = 50, message = "Please provide at least 50 characters explaining why you want to become a host")
    private String reason;

    @NotBlank(message = "Please describe the properties you plan to list")
    @Size(min = 50, message = "Please provide at least 50 characters describing your properties")
    private String propertyDescription;
}

