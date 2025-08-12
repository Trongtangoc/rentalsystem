package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.ChangePasswordRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.request.UpdateProfileRequest;
import com.codegym.projectmodule5.dto.response.UserInfoResponse;
import com.codegym.projectmodule5.entity.Role;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.exception.ResourceNotFoundException;
import com.codegym.projectmodule5.exception.UnauthorizedException;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())){
            throw new CustomException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException("Email is already taken");
        }
        if (userRepository.existsByPhone(request.getPhone())){
            throw new CustomException("Phone is already taken");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new CustomException("Passwords do not match");
        }

        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new CustomException("User Role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserInfoResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return convertToUserInfoResponse(user);
    }

    @Override
    public UserInfoResponse updateProfile(UpdateProfileRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if new username is taken by another user
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("Username is already taken");
        }

        // Check if new email is taken by another user
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email is already taken");
        }

        // Check if new phone is taken by another user
        if (!user.getPhone().equals(request.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new CustomException("Phone is already taken");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user = userRepository.save(user);
        return convertToUserInfoResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException("Current password is incorrect");
        }

        // Validate new password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public List<UserInfoResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserInfoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRole().getName().equals(RoleEnum.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only admins can delete users");
        }

        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Don't allow deleting other admins
        if (userToDelete.getRole().getName().equals(RoleEnum.ROLE_ADMIN)) {
            throw new CustomException("Cannot delete admin users");
        }

        userRepository.delete(userToDelete);
    }

    @Override
    public void promoteToHost(Long userId, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!admin.getRole().getName().equals(RoleEnum.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only admins can promote users to host");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role hostRole = roleRepository.findByName(RoleEnum.ROLE_HOST)
                .orElseThrow(() -> new CustomException("Host Role not found"));

        user.setRole(hostRole);
        userRepository.save(user);
    }

    private UserInfoResponse convertToUserInfoResponse(User user) {
        int totalHouses = user.getHouses() != null ? user.getHouses().size() : 0;
        int totalBookings = user.getBookings() != null ? user.getBookings().size() : 0;
        int totalReviews = user.getReviews() != null ? user.getReviews().size() : 0;

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().getName().name())
                .totalHouses(totalHouses)
                .totalBookings(totalBookings)
                .totalReviews(totalReviews)
                .build();
    }
}