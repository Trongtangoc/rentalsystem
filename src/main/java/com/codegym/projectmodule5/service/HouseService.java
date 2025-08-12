package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.housedto.request.CreateHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.FilterHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.UpdateHouseRequest;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseDetailResponse;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseListItemResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HouseService {
    HouseDetailResponse createHouse(CreateHouseRequest request, String username);
    HouseDetailResponse updateHouse(Long houseId, UpdateHouseRequest request, String username);
    void deleteHouse(Long houseId, String username);
    HouseDetailResponse getHouseById(Long houseId);
    Page<HouseListItemResponse> getAllHouses(FilterHouseRequest filter);
    List<HouseListItemResponse> getMyHouses(String username);
    List<HouseListItemResponse> searchHouses(String keyword);
}