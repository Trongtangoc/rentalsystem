package com.codegym.projectmodule5.dto.response.houseDTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseMapResponse {
    private Long id;
    private String title;
    private Double pricePerNight;
    private Double latitude;
    private Double longitude;
    private String mainImageUrl;
    private Double rating;
    private Integer reviewCount;
    private String propertyType;
}
