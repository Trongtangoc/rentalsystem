package com.codegym.projectmodule5.dto.response.payment;

import com.codegym.projectmodule5.enums.PaymentMethod;
import com.codegym.projectmodule5.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    
    private Long id;
    private String transactionId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String description;
    private String failureReason;
    
    // Booking information
    private Long bookingId;
    private String houseTitle;
    private String hostName;
    
    // User information
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Additional info
    private String paymentGatewayUrl;
    private boolean canBeRefunded;
    private String statusDisplayName;
    private String paymentMethodDisplayName;
}
