package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.housedto.request.CreateHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.FilterHouseRequest;
import com.codegym.projectmodule5.dto.request.housedto.request.UpdateHouseRequest;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseDetailResponse;
import com.codegym.projectmodule5.dto.response.houseDTOs.response.HouseListItemResponse;
import com.codegym.projectmodule5.dto.response.review.response.ReviewResponse;
import com.codegym.projectmodule5.entity.House;
import com.codegym.projectmodule5.entity.Image;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.HouseStatus;
import com.codegym.projectmodule5.exception.ResourceNotFoundException;
import com.codegym.projectmodule5.exception.UnauthorizedException;
import com.codegym.projectmodule5.repository.HouseRepository;
import com.codegym.projectmodule5.repository.ImageRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.HouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Override
    public HouseDetailResponse createHouse(CreateHouseRequest request, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        House house = House.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .address(request.getAddress())
                .status(HouseStatus.AVAILABLE)
                .owner(owner)
                .build();

        House savedHouse = houseRepository.save(house);

        // Save images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<Image> images = request.getImageUrls().stream()
                    .map(url -> Image.builder()
                            .url(url)
                            .house(savedHouse)
                            .build())
                    .collect(Collectors.toList());
            imageRepository.saveAll(images);
            savedHouse.setImages(images);
        }

        return convertToDetailResponse(savedHouse);
    }

    @Override
    public HouseDetailResponse updateHouse(Long houseId, UpdateHouseRequest request, String username) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!house.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only update your own houses");
        }

        house.setTitle(request.getTitle());
        house.setDescription(request.getDescription());
        house.setPrice(request.getPrice());
        house.setAddress(request.getAddress());

        // Update images
        if (request.getImageUrls() != null) {
            // Delete old images
            if (house.getImages() != null && !house.getImages().isEmpty()) {
                imageRepository.deleteAll(house.getImages());
            }

            // Create a final reference for use in lambda
            final House finalHouse = house;

            // Add new images
            List<Image> newImages = request.getImageUrls().stream()
                    .map(url -> Image.builder()
                            .url(url)
                            .house(finalHouse)
                            .build())
                    .collect(Collectors.toList());
            imageRepository.saveAll(newImages);
            house.setImages(newImages);
        }

        House savedHouse = houseRepository.save(house);
        return convertToDetailResponse(savedHouse);
    }

    @Override
    public void deleteHouse(Long houseId, String username) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!house.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own houses");
        }

        houseRepository.delete(house);
    }

    @Override
    public HouseDetailResponse getHouseById(Long houseId) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new ResourceNotFoundException("House not found"));
        return convertToDetailResponse(house);
    }

    @Override
    public Page<HouseListItemResponse> getAllHouses(FilterHouseRequest filter) {
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDirection()), filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Page<House> houses;

        // Nếu có filter thì dùng method có filter, không thì lấy tất cả AVAILABLE houses
        if (filter.getLocation() != null || filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            houses = houseRepository.findHousesWithFilters(
                    filter.getLocation(),
                    filter.getMinPrice(),
                    filter.getMaxPrice(),
                    HouseStatus.AVAILABLE,
                    pageable
            );
        } else {
            houses = houseRepository.findAllByStatus(HouseStatus.AVAILABLE, pageable);
        }

        return houses.map(this::convertToListItemResponse);
    }

    @Override
    public List<HouseListItemResponse> searchHouses(String keyword) {
        List<House> houses = houseRepository.findByKeywordAndStatus(keyword, HouseStatus.AVAILABLE);
        return houses.stream()
                .map(this::convertToListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HouseListItemResponse> getMyHouses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<House> houses = houseRepository.findAllByOwnerId(user.getId());
        return houses.stream()
                .map(this::convertToListItemResponse)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<HouseListItemResponse> searchHouses(String keyword) {
//        List<House> allHouses = houseRepository.findAll();
//        return allHouses.stream()
//                .filter(house -> house.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
//                        house.getAddress().toLowerCase().contains(keyword.toLowerCase()) ||
//                        house.getDescription().toLowerCase().contains(keyword.toLowerCase()))
//                .map(this::convertToListItemResponse)
//                .collect(Collectors.toList());
//    }

    private HouseDetailResponse convertToDetailResponse(House house) {
        List<String> imageUrls = house.getImages() != null ?
                house.getImages().stream().map(Image::getUrl).collect(Collectors.toList()) :
                new ArrayList<>();

        List<ReviewResponse> reviews = house.getReviews() != null ?
                house.getReviews().stream()
                        .map(review -> ReviewResponse.builder()
                                .id(review.getId())
                                .rating(review.getRating())
                                .comment(review.getComment())
                                .userName(review.getUser().getUsername())
                                .userId(review.getUser().getId())
                                .houseId(house.getId())
                                .houseTitle(house.getTitle())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>();

        double averageRating = reviews.stream()
                .mapToInt(ReviewResponse::getRating)
                .average()
                .orElse(0.0);

        return HouseDetailResponse.builder()
                .id(house.getId())
                .title(house.getTitle())
                .description(house.getDescription())
                .price(house.getPrice())
                .address(house.getAddress())
                .status(house.getStatus().name())
                .ownerId(house.getOwner().getId())
                .ownerName(house.getOwner().getUsername())
                .ownerPhone(house.getOwner().getPhone())
                .ownerEmail(house.getOwner().getEmail())
                .imageUrls(imageUrls)
                .reviews(reviews)
                .averageRating(averageRating)
                .reviewCount(reviews.size())
                .build();
    }

    private HouseListItemResponse convertToListItemResponse(House house) {
        List<String> imageUrls = house.getImages() != null ?
                house.getImages().stream().map(Image::getUrl).collect(Collectors.toList()) :
                new ArrayList<>();
        
        // Clean up image URLs - handle JSON string format
        imageUrls = imageUrls.stream()
                .map(this::cleanImageUrl)
                .filter(url -> url != null && !url.isEmpty())
                .collect(Collectors.toList());

        double averageRating = house.getReviews() != null ?
                house.getReviews().stream()
                        .mapToInt(review -> review.getRating())
                        .average()
                        .orElse(0.0) : 0.0;

        int reviewCount = house.getReviews() != null ? house.getReviews().size() : 0;

        return HouseListItemResponse.builder()
                .id(house.getId())
                .title(house.getTitle())
                .description(house.getDescription())
                .price(house.getPrice())
                .address(house.getAddress())
                .status(house.getStatus().name())
                .ownerName(house.getOwner().getUsername())
                .imageUrls(imageUrls)
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .build();
    }
    
    private String cleanImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        // Handle JSON array strings like [["/path"]] or ["/path"]
        if (url.startsWith("[[") && url.endsWith("]]")) {
            // Remove outer brackets and quotes
            url = url.substring(2, url.length() - 2);
            if (url.startsWith("\"") && url.endsWith("\"")) {
                url = url.substring(1, url.length() - 1);
            }
        } else if (url.startsWith("[") && url.endsWith("]")) {
            // Remove brackets and quotes
            url = url.substring(1, url.length() - 1);
            if (url.startsWith("\"") && url.endsWith("\"")) {
                url = url.substring(1, url.length() - 1);
            }
        }
        
        return url;
    }
}