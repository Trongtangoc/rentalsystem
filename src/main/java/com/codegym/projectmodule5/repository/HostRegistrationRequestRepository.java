package com.codegym.projectmodule5.repository;

import com.codegym.projectmodule5.entity.HostRegistrationRequest;
import com.codegym.projectmodule5.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostRegistrationRequestRepository extends JpaRepository<HostRegistrationRequest, Long> {
    List<HostRegistrationRequest> findByStatus(RequestStatus status);
    Optional<HostRegistrationRequest> findByUserIdAndStatus(Long userId, RequestStatus status);
    List<HostRegistrationRequest> findByUserId(Long userId);
    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);
}