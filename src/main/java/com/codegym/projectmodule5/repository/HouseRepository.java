package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findAllByOwnerId(Long ownerId);
}
