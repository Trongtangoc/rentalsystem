package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByHouseId(Long houseId);
}
