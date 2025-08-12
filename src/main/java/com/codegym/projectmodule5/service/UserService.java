package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.ChangePasswordRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.request.UpdateProfileRequest;
import com.codegym.projectmodule5.dto.response.UserInfoResponse;

import java.util.List;

public interface UserService {
    void register(RegisterRequest request);
    UserInfoResponse getUserProfile(String username);
    UserInfoResponse updateProfile(UpdateProfileRequest request, String username);
    void changePassword(ChangePasswordRequest request, String username);
    List<UserInfoResponse> getAllUsers();
    void deleteUser(Long userId, String adminUsername);
    void promoteToHost(Long userId, String adminUsername);
    void upgradeCurrentUserToHost(String username);

}