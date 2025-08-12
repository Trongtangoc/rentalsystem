package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.Review.request.ReviewRequest;
import com.codegym.projectmodule5.dto.response.review.response.ReviewResponse;
import com.codegym.projectmodule5.entity.House;
import com.codegym.projectmodule5.entity.Review;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.exception.ResourceNotFoundException;
import com.codegym.projectmodule5.exception.UnauthorizedException;
import com.codegym.projectmodule5.repository.HouseRepository;
import com.codegym.projectmodule5.repository.ReviewRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        House house = houseRepository.findById(request.getHouseId())
                .orElseThrow(() -> new ResourceNotFoundException("House not found"));

        // Check if user has already reviewed this house
        boolean hasReviewed = reviewRepository.findByHouseId(house.getId()).stream()
                .anyMatch(review -> review.getUser().getId().equals(user.getId()));

        if (hasReviewed) {
            throw new CustomException("You have already reviewed this house");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .house(house)
                .build();

        review = reviewRepository.save(review);
        return convertToResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        return convertToResponse(review);
    }

    @Override
    public void deleteReview(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return convertToResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByHouse(Long houseId) {
        List<Review> reviews = reviewRepository.findByHouseId(houseId);
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getMyReviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Review> reviews = reviewRepository.findByUserId(user.getId());
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .userName(review.getUser().getUsername())
                .userId(review.getUser().getId())
                .houseId(review.getHouse().getId())
                .houseTitle(review.getHouse().getTitle())
                .build();
    }
}