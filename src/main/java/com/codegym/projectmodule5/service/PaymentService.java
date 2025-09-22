package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.payment.PaymentRequest;
import com.codegym.projectmodule5.dto.response.payment.PaymentResponse;
import com.codegym.projectmodule5.entity.Payment;
import com.codegym.projectmodule5.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    
    PaymentResponse createPayment(PaymentRequest request, String username);
    
    PaymentResponse processPayment(String transactionId);
    
    PaymentResponse getPaymentByTransactionId(String transactionId);
    
    PaymentResponse getPaymentByBookingId(Long bookingId);
    
    List<PaymentResponse> getPaymentsByUser(String username);
    
    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
    
    PaymentResponse refundPayment(String transactionId, String reason);
    
    PaymentResponse cancelPayment(String transactionId, String reason);
    
    Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    List<PaymentResponse> getHostPayments(String hostUsername);
    
    // Mock payment processing methods
    PaymentResponse simulatePaymentSuccess(String transactionId);
    
    PaymentResponse simulatePaymentFailure(String transactionId, String reason);
}
