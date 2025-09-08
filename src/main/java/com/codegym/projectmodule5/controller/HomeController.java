package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.housedto.request.FilterHouseRequest;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseListItemResponse;
import com.codegym.projectmodule5.service.HouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final HouseService houseService;

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(defaultValue = "0") Integer page) {

        log.info("=== HOME CONTROLLER ===");
        log.info("Location: {}", location);
        log.info("MinPrice: {}", minPrice);
        log.info("MaxPrice: {}", maxPrice);
        log.info("Page: {}", page);

        try {
            FilterHouseRequest filter = new FilterHouseRequest();
            filter.setLocation(location);
            filter.setMinPrice(minPrice);
            filter.setMaxPrice(maxPrice);
            filter.setPage(page);
            filter.setSize(6); // Show 6 houses per page

            log.info("Filter created: {}", filter);

            Page<HouseListItemResponse> houses = houseService.getAllHouses(filter);

            log.info("Houses found: {}", houses.getTotalElements());
            log.info("Current page: {}", houses.getNumber());
            log.info("Total pages: {}", houses.getTotalPages());

            if (houses.hasContent()) {
                log.info("First house: {}", houses.getContent().get(0).getTitle());
            }

            model.addAttribute("houses", houses);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", houses.getTotalPages());
            model.addAttribute("location", location);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

            return "home";

        } catch (Exception e) {
            log.error("Error loading houses: ", e);
            model.addAttribute("error", "Không thể tải danh sách nhà: " + e.getMessage());
            model.addAttribute("houses", Page.empty());
            return "home";
        }
    }

    @GetMapping("/house-detail")
    public String houseDetail(@RequestParam Long id, Model model) {
        try {
            log.info("Loading house detail for ID: {}", id);
            var house = houseService.getHouseById(id);
            log.info("House loaded: {}", house.getTitle());
            model.addAttribute("house", house);
            return "house-detail";
        } catch (RuntimeException e) {
            log.error("Error loading house detail: ", e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}