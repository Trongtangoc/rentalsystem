package com.codegym.projectmodule5.dto.response.houseDTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseListItemResponse {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String address;
    private String status;
    private String ownerName;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer reviewCount;
}
