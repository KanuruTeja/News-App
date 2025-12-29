package com.example.NewsApp.dto;



import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReporterProfileResponse {

    private Long userId;
    private String name;
    private String email;
    private String city;
    private String state;
    private String zipCode;
    private String mobileNumber;
    private boolean enabled;
    private String idProofType;
    private Long idProofNumber;
    private int experience;
    private String specialization;

}