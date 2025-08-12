package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.Review.request.ReviewRequest;
import com.codegym.projectmodule5.dto.response.review.response.ReviewResponse;


import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, String username);
    ReviewResponse updateReview(Long reviewId, ReviewRequest request, String username);
    void deleteReview(Long reviewId, String username);
    ReviewResponse getReviewById(Long reviewId);
    List<ReviewResponse> getReviewsByHouse(Long houseId);
    List<ReviewResponse> getMyReviews(String username);
}