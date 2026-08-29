package com.example.booking.service;

import com.example.booking.dto.request.LoginRequest;
import com.example.booking.dto.request.RegisterRequest;
import com.example.booking.dto.response.ApiResponse;
import com.example.booking.dto.response.AuthResponse;
import com.example.booking.dto.response.UserProfileResponse;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    ApiResponse<UserProfileResponse> register(RegisterRequest registerRequest);

    UserProfileResponse getCurrentUserProfile(String username);
}
