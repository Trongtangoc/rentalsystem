package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail))
    }
}
