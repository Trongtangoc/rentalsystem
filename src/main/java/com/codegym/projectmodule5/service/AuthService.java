package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.LoginRequest;
import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.response.JwtResponse;

public interface AuthService {
    void register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    void logout(String token);
}