package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.AdvancedSearchRequest;
import com.codegym.projectmodule5.dto.request.MapSearchRequest;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseListItemResponse;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseMapResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {
    Page<HouseListItemResponse> advancedSearch(AdvancedSearchRequest request);
    List<HouseMapResponse> mapSearch(MapSearchRequest request);
    List<String> getPopularDestinations();
    List<String> getSuggestions(String query);
    List<HouseListItemResponse> getSimilarProperties(Long houseId);
    List<HouseListItemResponse> getRecommendedProperties(String username);
}