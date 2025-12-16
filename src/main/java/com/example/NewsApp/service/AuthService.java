package com.example.NewsApp.service;

import com.example.NewsApp.dto.LoginRequest;
import com.example.NewsApp.dto.RegisterRequest;
import com.example.NewsApp.dto.ResetPasswordRequest;
import com.example.NewsApp.dto.VerifyOtpRequest;
import com.example.NewsApp.entity.*;
import com.example.NewsApp.exception.ApiException;
import com.example.NewsApp.repository.*;
import com.example.NewsApp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional

public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final LocationRepository locationRepo;
    private final PasswordResetOtpRepository otpRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtProvider;

    // ================= REGISTER =================
    public User register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail()))
            throw new ApiException("Email already exists");

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .enabled(true)
                .build();
        userRepo.save(user);

        Role role = roleRepo.findById(req.getRoleId())
                .orElseThrow(() -> new ApiException("Invalid role"));
        userRoleRepo.save(new UserRole(null, user, role));

        locationRepo.save(
                Location.builder()
                        .city(req.getCity())
                        .state(req.getState())
                        .latitude(req.getLatitude())
                        .longitude(req.getLongitude())
                        .user(user)
                        .build()
        );

        return user;
    }

    // ================= LOGIN =================
    public String login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new ApiException("Invalid credentials");

        return jwtProvider.generateToken(user.getEmail());
    }

    // ================= SEND OTP =================
    public void sendOtp(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        PasswordResetOtp resetOtp =
                otpRepo.findByUser(user).orElse(new PasswordResetOtp());

        resetOtp.setUser(user);
        resetOtp.setOtp(otp);
        resetOtp.setExpiry(LocalDateTime.now().plusMinutes(5));
        resetOtp.setVerified(false);

        otpRepo.save(resetOtp); // INSERT or UPDATE safely

        emailService.send(
                email,
                "Password Reset OTP",
                "Your OTP is: " + otp
        );
    }

    // ================= VERIFY OTP =================
    public void verifyOtp(VerifyOtpRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));

        PasswordResetOtp otp = otpRepo.findByUser(user)
                .orElseThrow(() -> new ApiException("OTP not found"));

        if (otp.getExpiry().isBefore(LocalDateTime.now()))
            throw new ApiException("OTP expired");

        if (!otp.getOtp().equals(req.getOtp()))
            throw new ApiException("Invalid OTP");

        otp.setVerified(true);
        otpRepo.save(otp);
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(ResetPasswordRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));

        PasswordResetOtp otp = otpRepo.findByUser(user)
                .orElseThrow(() -> new ApiException("OTP not found"));

        if (!otp.isVerified())
            throw new ApiException("OTP not verified");

        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(user);

        otpRepo.delete(otp); // cleanup after success
    }
}
