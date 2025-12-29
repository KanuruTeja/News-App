package com.example.NewsApp.service;



import com.example.NewsApp.dto.*;
import com.example.NewsApp.entity.*;
import com.example.NewsApp.exception.ApiException;
import com.example.NewsApp.repository.*;
import com.example.NewsApp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final UserExperienceRepository userExperienceRepo;

    // ================= REGISTER =================
    @Transactional
    public User register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .mobileNumber(req.getMobileNumber())
                .enabled(true)
                .authProvider(AuthProvider.LOCAL)
                .build();

        user = userRepo.saveAndFlush(user);

        Role role = roleRepo.findById(req.getRoleId())
                .orElseThrow(() ->
                        new ApiException("Invalid role", HttpStatus.BAD_REQUEST)
                );

        userRoleRepo.save(
                UserRole.builder()
                        .id(new UserRoleId(user.getId(), role.getId()))
                        .user(user)
                        .role(role)
                        .build()
        );

        locationRepo.save(
                Location.builder()
                        .city(req.getCity())
                        .state(req.getState())
                        .latitude(req.getLatitude())
                        .longitude(req.getLongitude())
                        .zipCode(req.getZipCode())
                        .user(user)
                        .build()
        );

        // ✅ SAVE EXPERIENCE ONLY IF USER PROVIDED DATA
        if (req.getIdProofType() != null
                || req.getIdProofNumber() != null
                || req.getExperience() != null
                || req.getSpecialization() != null) {

            userExperienceRepo.save(
                    UserExperience.builder()
                            .user(user)
                            .idProofType(req.getIdProofType())
                            .idProofNumber(req.getIdProofNumber())
                            .experience(req.getExperience())
                            .specialization(req.getSpecialization())
                            .build()
            );
        }

        return user;
    }



    // ================= LOGIN =================
    public LoginResponse login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED)
                );

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        Role role = userRoleRepo.findByUser(user)
                .orElseThrow(() ->
                        new ApiException("Role not found", HttpStatus.FORBIDDEN)
                )
                .getRole();

        String token =jwtProvider.generateToken(
                user.getEmail(),
                role.getName()	
        );
        return new LoginResponse(
        		user.getId(),
        		role.getName(),
        		token);
        		
    }

    // ================= SEND OTP =================
    public void sendOtp(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND)
                );

        String otp = String.valueOf(1000 + new Random().nextInt(9000));

        PasswordResetOtp resetOtp =
                otpRepo.findByUser(user).orElse(new PasswordResetOtp());

        resetOtp.setUser(user);
        resetOtp.setOtp(otp);
        resetOtp.setExpiry(LocalDateTime.now().plusMinutes(5));
        resetOtp.setVerified(false);

        otpRepo.save(resetOtp);

        emailService.send(
                email,
                "Password Reset OTP",
                "Your OTP is: " + otp
        );
    }

    // ================= VERIFY OTP =================
    public void verifyOtp(VerifyOtpRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND)
                );

        PasswordResetOtp otp = otpRepo.findByUser(user)
                .orElseThrow(() ->
                        new ApiException("OTP not found", HttpStatus.NOT_FOUND)
                );

        if (otp.getExpiry().isBefore(LocalDateTime.now())) {
            throw new ApiException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if (!otp.getOtp().equals(req.getOtp())) {
            throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

        otp.setVerified(true);
        otpRepo.save(otp);
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(ResetPasswordRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND)
                );

        PasswordResetOtp otp = otpRepo.findByUser(user)
                .orElseThrow(() ->
                        new ApiException("OTP not found", HttpStatus.NOT_FOUND)
                );

        if (!otp.isVerified()) {
            throw new ApiException("OTP not verified", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(user);

        otpRepo.delete(otp);
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND)
                );

        // DELETE CHILD TABLES FIRST
        otpRepo.deleteByUser(user);
        locationRepo.deleteByUser(user);
        userRoleRepo.deleteByUser(user);


        userRepo.delete(user);
    }


//    ==================OAuth====================
    public void completeProfile(String email, CompleteProfileRequest req) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (user.isProfileCompleted()) {
            throw new ApiException("Profile already completed", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepo.findById(3)
                .orElseThrow(() ->
                        new ApiException("Invalid role", HttpStatus.BAD_REQUEST));

        user.setMobileNumber(req.getMobileNumber());
        user.setProfileCompleted(true);
        userRepo.save(user);

        userRoleRepo.save(
                UserRole.builder()
                        .id(new UserRoleId(user.getId(), role.getId()))
                        .user(user)
                        .role(role)
                        .build()
        );

        locationRepo.save(
                Location.builder()
                        .city(req.getCity())
                        .state(req.getState())
                        .zipCode(req.getZipCode())
                        .latitude(req.getLatitude())
                        .longitude(req.getLongitude())
                        .user(user)
                        .build()
        );
    }
}
