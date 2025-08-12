package com.codegym.projectmodule5.dto.response.houseDTOs.response;

import com.codegym.projectmodule5.dto.response.review.response.ReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String address;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private List<String> imageUrls;
    private List<ReviewResponse> reviews;
    private Double averageRating;
    private Integer reviewCount;
}