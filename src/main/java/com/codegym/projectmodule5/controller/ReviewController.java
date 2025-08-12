package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.request.Review.request.ReviewRequest;
import com.codegym.projectmodule5.dto.response.review.response.ReviewResponse;
import com.codegym.projectmodule5.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        try {
            ReviewResponse response = reviewService.createReview(request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        try {
            ReviewResponse response = reviewService.updateReview(reviewId, request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        try {
            reviewService.deleteReview(reviewId, authentication.getName());
            return ResponseEntity.ok(new ApiResponse(true, "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long reviewId) {
        try {
            ReviewResponse response = reviewService.getReviewById(reviewId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/house/{houseId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByHouse(@PathVariable Long houseId) {
        try {
            List<ReviewResponse> reviews = reviewService.getReviewsByHouse(houseId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication authentication) {
        try {
            List<ReviewResponse> reviews = reviewService.getMyReviews(authentication.getName());
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}