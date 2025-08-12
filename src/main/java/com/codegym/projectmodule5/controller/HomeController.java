package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.housedto.request.FilterHouseRequest;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseListItemResponse;
import com.codegym.projectmodule5.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HouseService houseService;

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(defaultValue = "0") Integer page) {

        FilterHouseRequest filter = new FilterHouseRequest();
        filter.setLocation(location);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setPage(page);
        filter.setSize(6); // Show 6 houses per page

        Page<HouseListItemResponse> houses = houseService.getAllHouses(filter);

        model.addAttribute("houses", houses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", houses.getTotalPages());
        model.addAttribute("location", location);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "home";
    }

    @GetMapping("/house-detail")
    public String houseDetail(@RequestParam Long id, Model model) {
        try {
            var house = houseService.getHouseById(id);
            model.addAttribute("house", house);
            return "house-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}