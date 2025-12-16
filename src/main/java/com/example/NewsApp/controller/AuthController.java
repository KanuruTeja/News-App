package com.example.NewsApp.controller;

import com.example.NewsApp.dto.*;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @RequestBody RegisterRequest req) {

        User user = authService.register(req);

        return ResponseEntity.status(201).body(
                new ApiResponse<>(
                        "Registration successful",
                        false,
                        201,
                        Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "role", req.getRoleId()
                        )
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @RequestBody LoginRequest req) {

        String token = authService.login(req);

        return ResponseEntity.ok(
                new ApiResponse<>("Login success", false, 200, token)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgot(
            @RequestBody ForgotPasswordRequest req) {

        authService.sendOtp(req.getEmail());
        return ResponseEntity.ok(
                new ApiResponse<>("OTP sent", false, 200, null)
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(
            @RequestBody VerifyOtpRequest req) {

        authService.verifyOtp(req);
        return ResponseEntity.ok(
                new ApiResponse<>("OTP verified", false, 200, null)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> reset(
            @RequestBody ResetPasswordRequest req) {

        authService.resetPassword(req);
        return ResponseEntity.ok(
                new ApiResponse<>("Password reset successful", false, 200, null)
        );
    }
}
