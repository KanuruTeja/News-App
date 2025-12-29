package com.example.NewsApp.dto;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private boolean reporterApproved;
    private boolean enabled;
    private boolean profileCompleted;

    private String city;
    private String state;

    private String idProofType;
    private String idProofNumber;
    private String experience;
    private String specialization;
}

