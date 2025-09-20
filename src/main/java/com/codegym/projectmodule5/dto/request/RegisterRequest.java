package com.codegym.projectmodule5.dto.request;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be 3-32 chars")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Username can include letters, digits, dot, underscore, hyphen")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 254, message = "Email too long")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(min = 7, max = 20, message = "Phone must be 7-20 digits")
    @Pattern(regexp = "^\\+?\\d{7,20}$", message = "Phone must be digits, optional leading +")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 32, message = "Password must be 6-32 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Confirm password is required")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String confirmPassword;

    // >>> Cross-field validation, no custom annotation needed
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
