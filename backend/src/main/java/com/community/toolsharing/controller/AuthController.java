package com.community.toolsharing.controller;

import com.community.toolsharing.dto.ApiResponse;
import com.community.toolsharing.dto.LoginRequest;
import com.community.toolsharing.dto.ProfileUpdateRequest;
import com.community.toolsharing.dto.RegisterRequest;
import com.community.toolsharing.model.User;
import com.community.toolsharing.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ApiResponse.success("Registration successful", user);
    }

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ApiResponse.success("Login successful", token);
    }

    @GetMapping("/profile")
    public ApiResponse<User> getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getProfile(username);
        return ApiResponse.success(user);
    }

    @PutMapping("/profile")
    public ApiResponse<User> updateProfile(@RequestBody ProfileUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.updateProfile(username, request);
        return ApiResponse.success("Profile updated successfully", user);
    }
}
