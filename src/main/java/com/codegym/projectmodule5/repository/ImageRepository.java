package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByHouseId(Long houseId);
}
