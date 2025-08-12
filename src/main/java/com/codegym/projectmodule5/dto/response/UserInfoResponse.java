package com.codegym.projectmodule5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private Integer totalHouses;
    private Integer totalBookings;
    private Integer totalReviews;
}