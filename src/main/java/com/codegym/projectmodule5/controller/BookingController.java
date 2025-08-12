package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.booking.BookingRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.BookingDetailResponse;
import com.codegym.projectmodule5.dto.response.BookingListItemResponse;
import com.codegym.projectmodule5.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDetailResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {
        try {
            BookingDetailResponse response = bookingService.createBooking(request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingDetailResponse> confirmBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        try {
            BookingDetailResponse response = bookingService.confirmBooking(bookingId, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingDetailResponse> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        try {
            BookingDetailResponse response = bookingService.cancelBooking(bookingId, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailResponse> getBookingById(
            @PathVariable Long bookingId,
            Authentication authentication) {
        try {
            BookingDetailResponse response = bookingService.getBookingById(bookingId, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingListItemResponse>> getMyBookings(Authentication authentication) {
        try {
            List<BookingListItemResponse> bookings = bookingService.getMyBookings(authentication.getName());
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my-houses/bookings")
    public ResponseEntity<List<BookingListItemResponse>> getBookingsForMyHouses(Authentication authentication) {
        try {
            List<BookingListItemResponse> bookings = bookingService.getBookingsForMyHouses(authentication.getName());
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}