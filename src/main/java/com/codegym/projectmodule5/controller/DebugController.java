// DebugController.java - For testing endpoints
package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.entity.House;
import com.codegym.projectmodule5.entity.Image;
import com.codegym.projectmodule5.repository.HouseRepository;
import com.codegym.projectmodule5.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final HouseRepository houseRepository;
    private final ImageRepository imageRepository;

    @GetMapping("/debug/status")
    public Map<String, Object> getStatus() {
        log.info("Debug status endpoint called");
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("message", "Application is running");
        status.put("timestamp", System.currentTimeMillis());
        return status;
    }

    @GetMapping("/debug/houses")
    public Map<String, Object> getHousesDebug() {
        log.info("Debug houses endpoint called");
        Map<String, Object> result = new HashMap<>();
        
        List<House> houses = houseRepository.findAll();
        List<Image> images = imageRepository.findAll();
        
        result.put("totalHouses", houses.size());
        result.put("totalImages", images.size());
        result.put("houses", houses);
        result.put("images", images);
        
        return result;
    }

    @GetMapping("/debug/fix-images")
    public Map<String, Object> fixImages() {
        log.info("Fixing images...");
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Delete all existing images
            imageRepository.deleteAll();
            
            // Get all houses
            List<House> houses = houseRepository.findAll();
            
            // Add images to each house
            for (House house : houses) {
                Image image1 = Image.builder()
                        .url("/uploads/images/1.png")
                        .house(house)
                        .build();
                Image image2 = Image.builder()
                        .url("/uploads/images/2.webp")
                        .house(house)
                        .build();
                
                imageRepository.save(image1);
                imageRepository.save(image2);
            }
            
            result.put("success", true);
            result.put("message", "Images fixed successfully");
            result.put("housesUpdated", houses.size());
            result.put("imagesCreated", houses.size() * 2);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/debug/auth")
    public Map<String, Object> getAuthStatus() {
        log.info("Debug auth endpoint called");
        Map<String, Object> status = new HashMap<>();
        status.put("loginUrl", "/auth/login");
        status.put("registerUrl", "/auth/register");
        status.put("apiLoginUrl", "/api/auth/login");
        status.put("message", "Auth endpoints available");
        return status;
    }
}