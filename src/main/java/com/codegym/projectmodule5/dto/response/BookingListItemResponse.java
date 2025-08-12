package com.codegym.projectmodule5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListItemResponse {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String houseTitle;
    private String houseAddress;
    private Double housePrice;
    private String ownerName;
    private String guestName;
    private Double totalPrice;
}