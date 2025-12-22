package com.example.NewsApp.controller;

import com.example.NewsApp.dto.*;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterRequest req) {

        User user = authService.register(req);

        return ResponseEntity.status(201).body(
                new ApiResponse<>(
                        "Registration successful",
                        Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "role", req.getRoleId()
                        )
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest req) {

        String token = authService.login(req);

        return ResponseEntity.ok(
                new ApiResponse<>("Login success", token)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgot(@RequestBody ForgotPasswordRequest req) {

        authService.sendOtp(req.getEmail());

        return ResponseEntity.ok(
                new ApiResponse<>("OTP sent", null)
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody VerifyOtpRequest req) {

        authService.verifyOtp(req);

        return ResponseEntity.ok(
                new ApiResponse<>("OTP verified", null)
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> reset(@RequestBody ResetPasswordRequest req) {

        authService.resetPassword(req);

        return ResponseEntity.ok(
                new ApiResponse<>("Password reset successful", null)
        );
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {

        authService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>("User deleted successfully", null)
        );
    }


    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "ADMIN ACCESS ONLY";
    }

    @GetMapping("/reporter/dashboard")
    @PreAuthorize("hasRole('REPORTER')")
    public String reporterDashboard() {
        return "REPORTER ACCESS ONLY";
    }


//    http://localhost:8080/oauth2/authorization/google

    @PostMapping("/complete-profile")
    public ResponseEntity<ApiResponse<Void>> completeProfile(
            @RequestBody CompleteProfileRequest request,
            Principal principal) {

        authService.completeProfile(principal.getName(), request);

        return ResponseEntity.ok(
                new ApiResponse<>("Profile completed successfully", null)
        );
    }


}
