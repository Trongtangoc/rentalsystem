package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.booking.BookingRequest;
import com.codegym.projectmodule5.dto.response.BookingDetailResponse;
import com.codegym.projectmodule5.dto.response.BookingListItemResponse;

import java.util.List;

public interface BookingService {
    BookingDetailResponse createBooking(BookingRequest request, String username);
    BookingDetailResponse confirmBooking(Long bookingId, String username);
    BookingDetailResponse cancelBooking(Long bookingId, String username);
    BookingDetailResponse getBookingById(Long bookingId, String username);
    List<BookingListItemResponse> getMyBookings(String username);
    List<BookingListItemResponse> getBookingsForMyHouses(String username);
}