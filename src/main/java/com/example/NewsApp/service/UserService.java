package com.example.NewsApp.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.NewsApp.dto.UserResponseDto;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.UserRole;
import com.example.NewsApp.repository.UserRepository;
import com.example.NewsApp.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

   

    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(u -> UserResponseDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .mobileNumber(u.getMobileNumber())
                .reporterApproved(u.isReporterApproved())
                .enabled(u.isEnabled())
                .profileCompleted(u.isProfileCompleted())
                .city(u.getLocation() != null ? u.getLocation().getCity() : null)
                .state(u.getLocation() != null ? u.getLocation().getState() : null)
                .idProofType(u.getUserExperience() != null ? u.getUserExperience().getIdProofType() : null)
                .idProofNumber(u.getUserExperience() != null ? u.getUserExperience().getIdProofNumber() : null)
                .experience(u.getUserExperience() != null ? u.getUserExperience().getExperience() : null)
                .specialization(u.getUserExperience() != null ? u.getUserExperience().getSpecialization() : null)
                .build());
    }

}