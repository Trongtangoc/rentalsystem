package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.Booking;
import com.codegym.projectmodule5.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    List<Review> findByHouseId(Long houseId);
}
