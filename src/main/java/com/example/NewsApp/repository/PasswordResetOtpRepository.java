package com.example.NewsApp.repository;

import com.example.NewsApp.entity.PasswordResetOtp;
import com.example.NewsApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByUser(User user);
}

