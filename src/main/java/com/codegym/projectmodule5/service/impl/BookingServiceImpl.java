package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.booking.BookingRequest;
import com.codegym.projectmodule5.dto.response.BookingDetailResponse;
import com.codegym.projectmodule5.dto.response.BookingListItemResponse;
import com.codegym.projectmodule5.entity.Booking;
import com.codegym.projectmodule5.entity.House;
import com.codegym.projectmodule5.entity.Image;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.BookingStatus;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.exception.ResourceNotFoundException;
import com.codegym.projectmodule5.exception.UnauthorizedException;
import com.codegym.projectmodule5.repository.BookingRepository;
import com.codegym.projectmodule5.repository.HouseRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDetailResponse createBooking(BookingRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        House house = houseRepository.findById(request.getHouseId())
                .orElseThrow(() -> new ResourceNotFoundException("House not found"));

        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException("Start date must be before end date");
        }

        if (request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new CustomException("Start date cannot be in the past");
        }

        // Check if house is available for the requested dates
        boolean hasConflict = bookingRepository.findByHouseId(house.getId()).stream()
                .anyMatch(booking -> booking.getStatus() == BookingStatus.CONFIRMED &&
                        isDateRangeOverlap(request.getStartDate(), request.getEndDate(),
                                booking.getStartDate(), booking.getEndDate()));

        if (hasConflict) {
            throw new CustomException("House is not available for the selected dates");
        }

        Booking booking = Booking.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.PENDING)
                .house(house)
                .user(user)
                .build();

        booking = bookingRepository.save(booking);
        return convertToDetailResponse(booking);
    }

    @Override
    public BookingDetailResponse confirmBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Only house owner can confirm bookings
        if (!booking.getHouse().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("Only house owner can confirm bookings");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        return convertToDetailResponse(booking);
    }

    @Override
    public BookingDetailResponse cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Both guest and host can cancel bookings
        boolean isGuest = booking.getUser().getId().equals(user.getId());
        boolean isOwner = booking.getHouse().getOwner().getId().equals(user.getId());

        if (!isGuest && !isOwner) {
            throw new UnauthorizedException("You can only cancel your own bookings or bookings for your houses");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.DONE) {
            throw new CustomException("Booking cannot be cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        return convertToDetailResponse(booking);
    }

    @Override
    public BookingDetailResponse getBookingById(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user has access to this booking
        boolean isGuest = booking.getUser().getId().equals(user.getId());
        boolean isOwner = booking.getHouse().getOwner().getId().equals(user.getId());

        if (!isGuest && !isOwner) {
            throw new UnauthorizedException("You don't have access to this booking");
        }

        return convertToDetailResponse(booking);
    }

    @Override
    public List<BookingListItemResponse> getMyBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return bookings.stream()
                .map(this::convertToListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingListItemResponse> getBookingsForMyHouses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<House> userHouses = houseRepository.findAllByOwnerId(user.getId());

        return userHouses.stream()
                .flatMap(house -> bookingRepository.findByHouseId(house.getId()).stream())
                .map(this::convertToListItemResponse)
                .collect(Collectors.toList());
    }

    private boolean isDateRangeOverlap(LocalDateTime start1, LocalDateTime end1,
                                       LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private BookingDetailResponse convertToDetailResponse(Booking booking) {
        House house = booking.getHouse();
        User owner = house.getOwner();
        User guest = booking.getUser();

        List<String> imageUrls = house.getImages() != null ?
                house.getImages().stream().map(Image::getUrl).collect(Collectors.toList()) :
                List.of();

        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        double totalPrice = days * house.getPrice();

        return BookingDetailResponse.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .status(booking.getStatus().name())
                .houseId(house.getId())
                .houseTitle(house.getTitle())
                .houseDescription(house.getDescription())
                .houseAddress(house.getAddress())
                .housePrice(house.getPrice())
                .houseImageUrls(imageUrls)
                .ownerId(owner.getId())
                .ownerName(owner.getUsername())
                .ownerPhone(owner.getPhone())
                .ownerEmail(owner.getEmail())
                .guestId(guest.getId())
                .guestName(guest.getUsername())
                .guestPhone(guest.getPhone())
                .guestEmail(guest.getEmail())
                .totalPrice(totalPrice)
                .build();
    }

    private BookingListItemResponse convertToListItemResponse(Booking booking) {
        House house = booking.getHouse();
        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        double totalPrice = days * house.getPrice();

        return BookingListItemResponse.builder()
                .id(booking.getId())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .status(booking.getStatus().name())
                .houseTitle(house.getTitle())
                .houseAddress(house.getAddress())
                .housePrice(house.getPrice())
                .ownerName(house.getOwner().getUsername())
                .guestName(booking.getUser().getUsername())
                .totalPrice(totalPrice)
                .build();
    }
}