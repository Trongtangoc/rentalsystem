package com.codegym.projectmodule5.service;

import com.codegym.projectmodule5.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}
