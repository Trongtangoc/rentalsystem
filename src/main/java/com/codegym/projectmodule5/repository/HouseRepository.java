// HouseRepository.java - Cần thêm các method filter
package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.House;
import com.codegym.projectmodule5.enums.HouseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findAllByOwnerId(Long ownerId);

    // Tìm tất cả houses có status AVAILABLE
    Page<House> findAllByStatus(HouseStatus status, Pageable pageable);

    // Filter với location và price range
    @Query("SELECT h FROM House h WHERE " +
            "(:location IS NULL OR LOWER(h.address) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:minPrice IS NULL OR h.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR h.price <= :maxPrice) AND " +
            "h.status = :status")
    Page<House> findHousesWithFilters(
            @Param("location") String location,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("status") HouseStatus status,
            Pageable pageable
    );

    // Search houses by keyword
    @Query("SELECT h FROM House h WHERE " +
            "(LOWER(h.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(h.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "h.status = :status")
    List<House> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") HouseStatus status);
}