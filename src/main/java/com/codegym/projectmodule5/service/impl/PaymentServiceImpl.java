package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.payment.PaymentRequest;
import com.codegym.projectmodule5.dto.response.payment.PaymentResponse;
import com.codegym.projectmodule5.entity.Booking;
import com.codegym.projectmodule5.entity.Payment;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.BookingStatus;
import com.codegym.projectmodule5.enums.PaymentStatus;
import com.codegym.projectmodule5.exception.ResourceNotFoundException;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.repository.BookingRepository;
import com.codegym.projectmodule5.repository.PaymentRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request, String username) {
        log.info("Creating payment for user: {} and booking: {}", username, request.getBookingId());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + request.getBookingId()));

        // Validate booking belongs to user
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new CustomException("You can only create payments for your own bookings");
        }

        // Check if booking already has a payment
        if (booking.getPayment() != null) {
            throw new CustomException("Payment already exists for this booking");
        }

        // Validate booking status
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new CustomException("Can only create payment for pending bookings");
        }

        // Validate amount matches booking total
        if (request.getAmount().compareTo(booking.getTotalAmount()) != 0) {
            throw new CustomException("Payment amount must match booking total amount");
        }

        Payment payment = Payment.builder()
                .transactionId(generateTransactionId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .description(request.getDescription())
                .booking(booking)
                .user(user)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created with transaction ID: {}", payment.getTransactionId());

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse processPayment(String transactionId) {
        log.info("Processing payment: {}", transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomException("Payment is not in pending status");
        }

        // Simulate payment processing
        try {
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);

            // Simulate external payment gateway call
            Thread.sleep(2000); // Simulate processing time

            // For demo purposes, randomly succeed or fail
            boolean success = Math.random() > 0.1; // 90% success rate

            if (success) {
                return simulatePaymentSuccess(transactionId);
            } else {
                return simulatePaymentFailure(transactionId, "Payment declined by bank");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return simulatePaymentFailure(transactionId, "Payment processing interrupted");
        }
    }

    @Override
    public PaymentResponse simulatePaymentSuccess(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionId));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());

        // Update booking status
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);
        log.info("Payment completed successfully: {}", transactionId);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse simulatePaymentFailure(String transactionId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setProcessedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);
        log.warn("Payment failed: {} - {}", transactionId, reason);

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionId));
        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for booking: " + bookingId));
        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatusOrderByCreatedAtDesc(status);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse refundPayment(String transactionId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionId));

        if (!payment.getStatus().canBeRefunded()) {
            throw new CustomException("Payment cannot be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason(reason);
        payment.setProcessedAt(LocalDateTime.now());

        // Update booking status
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason("Payment refunded: " + reason);

        payment = paymentRepository.save(payment);
        log.info("Payment refunded: {} - {}", transactionId, reason);

        return mapToResponse(payment);
    }

    @Override
    public PaymentResponse cancelPayment(String transactionId, String reason) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomException("Only pending payments can be cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setFailureReason(reason);
        payment.setProcessedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);
        log.info("Payment cancelled: {} - {}", transactionId, reason);

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Double revenue = paymentRepository.getTotalRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getHostPayments(String hostUsername) {
        User host = userRepository.findByUsername(hostUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Host not found: " + hostUsername));

        List<Payment> payments = paymentRepository.findCompletedPaymentsByHostId(host.getId());
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .processedAt(payment.getProcessedAt())
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .bookingId(payment.getBooking().getId())
                .houseTitle(payment.getBooking().getHouse().getTitle())
                .hostName(payment.getBooking().getHouse().getUser().getFullName())
                .userId(payment.getUser().getId())
                .userName(payment.getUser().getUsername())
                .userEmail(payment.getUser().getEmail())
                .canBeRefunded(payment.getStatus().canBeRefunded())
                .statusDisplayName(payment.getStatus().getDisplayName())
                .paymentMethodDisplayName(payment.getPaymentMethod().getDisplayName())
                .build();
    }

    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
