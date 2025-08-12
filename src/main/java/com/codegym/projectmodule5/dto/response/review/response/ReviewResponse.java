package com.codegym.projectmodule5.dto.response.review.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String userName;
    private Long userId;
    private Long houseId;
    private String houseTitle;
}