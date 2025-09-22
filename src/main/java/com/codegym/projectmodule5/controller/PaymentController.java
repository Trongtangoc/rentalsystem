package com.codegym.projectmodule5.controller;

import com.codegym.projectmodule5.dto.request.payment.PaymentRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.dto.response.payment.PaymentResponse;
import com.codegym.projectmodule5.enums.PaymentStatus;
import com.codegym.projectmodule5.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {
        
        log.info("Creating payment for user: {}", authentication.getName());
        PaymentResponse response = paymentService.createPayment(request, authentication.getName());
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment created successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{transactionId}/process")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @PathVariable String transactionId) {
        
        log.info("Processing payment: {}", transactionId);
        PaymentResponse response = paymentService.processPayment(transactionId);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment processed")
                .data(response)
                .build());
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        
        PaymentResponse response = paymentService.getPaymentByTransactionId(transactionId);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByBookingId(
            @PathVariable Long bookingId) {
        
        PaymentResponse response = paymentService.getPaymentByBookingId(bookingId);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment retrieved successfully")
                .data(response)
                .build());
    }

    @GetMapping("/my-payments")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments(
            Authentication authentication) {
        
        List<PaymentResponse> responses = paymentService.getPaymentsByUser(authentication.getName());
        
        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {
        
        List<PaymentResponse> responses = paymentService.getPaymentsByStatus(status);
        
        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Payments retrieved successfully")
                .data(responses)
                .build());
    }

    @PostMapping("/{transactionId}/refund")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable String transactionId,
            @RequestParam String reason) {
        
        log.info("Refunding payment: {} with reason: {}", transactionId, reason);
        PaymentResponse response = paymentService.refundPayment(transactionId, reason);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment refunded successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{transactionId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(
            @PathVariable String transactionId,
            @RequestParam String reason) {
        
        log.info("Cancelling payment: {} with reason: {}", transactionId, reason);
        PaymentResponse response = paymentService.cancelPayment(transactionId, reason);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment cancelled successfully")
                .data(response)
                .build());
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Double>> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Double revenue = paymentService.getTotalRevenue(startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.<Double>builder()
                .success(true)
                .message("Revenue calculated successfully")
                .data(revenue)
                .build());
    }

    @GetMapping("/host-payments")
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getHostPayments(
            Authentication authentication) {
        
        List<PaymentResponse> responses = paymentService.getHostPayments(authentication.getName());
        
        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("Host payments retrieved successfully")
                .data(responses)
                .build());
    }

    // Mock payment simulation endpoints for testing
    @PostMapping("/{transactionId}/simulate-success")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> simulatePaymentSuccess(
            @PathVariable String transactionId) {
        
        log.info("Simulating payment success: {}", transactionId);
        PaymentResponse response = paymentService.simulatePaymentSuccess(transactionId);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment simulation completed successfully")
                .data(response)
                .build());
    }

    @PostMapping("/{transactionId}/simulate-failure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> simulatePaymentFailure(
            @PathVariable String transactionId,
            @RequestParam(defaultValue = "Simulated failure") String reason) {
        
        log.info("Simulating payment failure: {} with reason: {}", transactionId, reason);
        PaymentResponse response = paymentService.simulatePaymentFailure(transactionId, reason);
        
        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment simulation completed with failure")
                .data(response)
                .build());
    }
}
