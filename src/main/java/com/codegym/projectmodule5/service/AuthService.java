package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;

public interface AuthService {
    ApiResponse register(RegisterRequest request);
}
