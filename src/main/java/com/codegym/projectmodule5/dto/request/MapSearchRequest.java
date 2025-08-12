package com.codegym.projectmodule5.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MapSearchRequest {
    private Double northEastLat;
    private Double northEastLng;
    private Double southWestLat;
    private Double southWestLng;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Double minPrice;
    private Double maxPrice;
}