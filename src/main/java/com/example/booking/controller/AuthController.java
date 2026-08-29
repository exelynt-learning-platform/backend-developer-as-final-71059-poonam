package com.example.booking.controller;

import com.example.booking.dto.request.LoginRequest;
import com.example.booking.dto.request.RegisterRequest;
import com.example.booking.dto.response.ApiResponse;
import com.example.booking.dto.response.AuthResponse;
import com.example.booking.dto.response.UserProfileResponse;
import com.example.booking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and account registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping({"/auth/login", "/api/auth/login"})
    @Operation(summary = "Login user and generate JWT token", description = "Authenticates user credentials and returns a Bearer JWT token.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authResponse));
    }

    @PostMapping({"/auth/register", "/api/auth/register"})
    @Operation(summary = "Register a new user account", description = "Creates a new user account with default ROLE_USER or specified role.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<UserProfileResponse> response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping({"/auth/me", "/api/auth/me"})
    @Operation(summary = "Get current authenticated user profile", description = "Returns user profile details extracted from active JWT.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = authService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved", profile));
    }
}
