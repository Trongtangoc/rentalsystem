package com.codegym.projectmodule5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Long houseId;
    private String houseTitle;
    private String houseDescription;
    private String houseAddress;
    private Double housePrice;
    private List<String> houseImageUrls;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private Long guestId;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private Double totalPrice;
    private LocalDateTime createdAt;
}
