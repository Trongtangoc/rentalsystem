package com.codegym.projectmodule5.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AdvancedSearchRequest {
    // Location
    private String destination;
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;

    // Dates
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;

    // Property details
    private List<String> propertyTypes;
    private Integer minBedrooms;
    private Integer minBathrooms;
    private Integer minBeds;
    private Double minArea;
    private Double maxArea;

    // Price range
    private Double minPrice;
    private Double maxPrice;
    private Boolean includeServiceFees;

    // Amenities
    private List<Long> amenityIds;
    private Boolean hasWifi;
    private Boolean hasKitchen;
    private Boolean hasParking;
    private Boolean hasPet;
    private Boolean hasPool;
    private Boolean hasAirConditioning;

    // Booking options
    private Boolean instantBooking;
    private String cancellationPolicy;

    // Host preferences
    private Boolean superHost;
    private Boolean verifiedHost;
    private Double minHostRating;

    // Property ratings
    private Double minRating;
    private Integer minReviews;

    // Accessibility
    private Boolean wheelchairAccessible;

    // Sorting
    private String sortBy = "relevance"; // relevance, price_low, price_high, rating, distance
    private String sortDirection = "asc";

    // Pagination
    private Integer page = 0;
    private Integer size = 20;
}