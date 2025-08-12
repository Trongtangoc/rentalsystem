package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.housedto.request.CreateHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.FilterHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.UpdateHouseRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseDetailResponse;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseListItemResponse;
import com.codegym.projectmodule5.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/houses")
public class HouseController {

    private final HouseService houseService;

    @PostMapping
    public ResponseEntity<HouseDetailResponse> createHouse(
            @Valid @RequestBody CreateHouseRequest request,
            Authentication authentication) {
        try {
            HouseDetailResponse response = houseService.createHouse(request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{houseId}")
    public ResponseEntity<HouseDetailResponse> updateHouse(
            @PathVariable Long houseId,
            @Valid @RequestBody UpdateHouseRequest request,
            Authentication authentication) {
        try {
            HouseDetailResponse response = houseService.updateHouse(houseId, request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{houseId}")
    public ResponseEntity<ApiResponse> deleteHouse(
            @PathVariable Long houseId,
            Authentication authentication) {
        try {
            houseService.deleteHouse(houseId, authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "House deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{houseId}")
    public ResponseEntity<HouseDetailResponse> getHouseById(@PathVariable Long houseId) {
        try {
            HouseDetailResponse response = houseService.getHouseById(houseId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<Page<HouseListItemResponse>> getAllHouses(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        FilterHouseRequest filter = new FilterHouseRequest();
        filter.setLocation(location);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);
        filter.setPage(page);
        filter.setSize(size);

        Page<HouseListItemResponse> houses = houseService.getAllHouses(filter);
        return ResponseEntity.ok(houses);
    }

    @GetMapping("/my-houses")
    public ResponseEntity<List<HouseListItemResponse>> getMyHouses(Authentication authentication) {
        try {
            List<HouseListItemResponse> houses = houseService.getMyHouses(authentication.getName());
            return ResponseEntity.ok(houses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<HouseListItemResponse>> searchHouses(@RequestParam String keyword) {
        try {
            List<HouseListItemResponse> houses = houseService.searchHouses(keyword);
            return ResponseEntity.ok(houses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}