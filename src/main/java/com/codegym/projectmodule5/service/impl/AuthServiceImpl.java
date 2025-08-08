package com.codegym.projectmodule5.service.impl;

import com.codegym.projectmodule5.dto.request.RegisterRequest;
import com.codegym.projectmodule5.dto.response.ApiResponse;
import com.codegym.projectmodule5.entity.Role;
import com.codegym.projectmodule5.entity.User;
import com.codegym.projectmodule5.enums.RoleEnum;
import com.codegym.projectmodule5.exception.CustomException;
import com.codegym.projectmodule5.repository.RoleRepository;
import com.codegym.projectmodule5.repository.UserRepository;
import com.codegym.projectmodule5.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

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
}
