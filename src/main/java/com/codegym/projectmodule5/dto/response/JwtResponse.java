package com.codegym.projectmodule5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    @Builder.Default  // Thêm annotation này


    private String type = "Bearer";
    private String token;
    private String username;
    private String email;
    private String role;

    public JwtResponse(String token, String username, String email, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.type = "Bearer";
    }
}