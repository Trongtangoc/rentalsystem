package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.HostRegistrationRequestDto;
import com.codegym.projectmodule5.dto.response.HostRegistrationResponseDto;
import com.codegym.projectmodule5.entity.HostRegistrationRequest;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.RequestStatus;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.repository.HostRegistrationRequestRepository;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HostRegistrationService {

    private final HostRegistrationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;

    public HostRegistrationResponseDto submitHostRequest(HostRegistrationRequestDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));

        // Check if user already has a pending request
        if (requestRepository.existsByUserIdAndStatus(user.getId(), RequestStatus.PENDING)) {
            throw new CustomException("You already have a pending host registration request");
        }

        // Check if user is already a host
        if (user.getRole().getName() == RoleEnum.ROLE_HOST ||
                user.getRole().getName() == RoleEnum.ROLE_ADMIN) {
            throw new CustomException("You are already a host or admin");
        }

        // Create new request
        HostRegistrationRequest request = HostRegistrationRequest.builder()
                .user(user)
                .phoneNumber(dto.getPhoneNumber())
                .idCardNumber(dto.getIdCardNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .country(dto.getCountry())
                .reason(dto.getReason())
                .propertyDescription(dto.getPropertyDescription())
                .status(RequestStatus.PENDING)
                .build();

        request = requestRepository.save(request);

        // Notify admins
        notificationService.notifyAdmins("New host registration request from " + username);

        log.info("Host registration request submitted by user: {}", username);

        return convertToResponseDto(request);
    }

    public List<HostRegistrationResponseDto> getAllPendingRequests(String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException("Admin not found"));

        if (admin.getRole().getName() != RoleEnum.ROLE_ADMIN) {
            throw new CustomException("Only admins can view registration requests");
        }

        List<HostRegistrationRequest> requests = requestRepository.findByStatus(RequestStatus.PENDING);
        return requests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public HostRegistrationResponseDto approveRequest(Long requestId, String adminUsername, String notes) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException("Admin not found"));

        if (admin.getRole().getName() != RoleEnum.ROLE_ADMIN) {
            throw new CustomException("Only admins can approve requests");
        }

        HostRegistrationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new CustomException("This request has already been processed");
        }

        // Update request status
        request.setStatus(RequestStatus.APPROVED);
        request.setAdminNotes(notes);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());

        // Upgrade user to host
        User user = request.getUser();
        user.setRole(roleRepository.findByName(RoleEnum.ROLE_HOST)
                .orElseThrow(() -> new CustomException("Host role not found")));

        userRepository.save(user);
        request = requestRepository.save(request);

        // Notify user
        notificationService.createNotification(
                "Your host registration has been approved! Please log out and log in again to access host features.",
                com.codegym.projectmodule5.enums.NotificationType.SYSTEM,
                user.getId()
        );

        log.info("Host registration request {} approved by admin: {}", requestId, adminUsername);

        return convertToResponseDto(request);
    }

    public HostRegistrationResponseDto rejectRequest(Long requestId, String adminUsername, String reason) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException("Admin not found"));

        if (admin.getRole().getName() != RoleEnum.ROLE_ADMIN) {
            throw new CustomException("Only admins can reject requests");
        }

        HostRegistrationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new CustomException("This request has already been processed");
        }

        // Update request status
        request.setStatus(RequestStatus.REJECTED);
        request.setAdminNotes(reason);
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());

        request = requestRepository.save(request);

        // Notify user
        notificationService.createNotification(
                "Your host registration has been rejected. Reason: " + reason,
                com.codegym.projectmodule5.enums.NotificationType.SYSTEM,
                request.getUser().getId()
        );

        log.info("Host registration request {} rejected by admin: {}", requestId, adminUsername);

        return convertToResponseDto(request);
    }

    public HostRegistrationResponseDto getMyRequest(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));

        HostRegistrationRequest request = requestRepository.findByUserIdAndStatus(user.getId(), RequestStatus.PENDING)
                .orElse(null);

        if (request == null) {
            // Try to find any request
            List<HostRegistrationRequest> requests = requestRepository.findByUserId(user.getId());
            if (!requests.isEmpty()) {
                request = requests.get(0); // Get the most recent one
            }
        }

        return request != null ? convertToResponseDto(request) : null;
    }

    private HostRegistrationResponseDto convertToResponseDto(HostRegistrationRequest request) {
        return HostRegistrationResponseDto.builder()
                .id(request.getId())
                .username(request.getUser().getUsername())
                .email(request.getUser().getEmail())
                .phoneNumber(request.getPhoneNumber())
                .idCardNumber(request.getIdCardNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .reason(request.getReason())
                .propertyDescription(request.getPropertyDescription())
                .status(request.getStatus().name())
                .adminNotes(request.getAdminNotes())
                .reviewedBy(request.getReviewedBy() != null ? request.getReviewedBy().getUsername() : null)
                .createdAt(request.getCreatedAt())
                .reviewedAt(request.getReviewedAt())
                .build();
    }
}