package com.example.NewsApp.dto;

import lombok.Data;

@Data
public class CompleteProfileRequest {

    private Long roleId;
    private String mobileNumber;
    private String city;
    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;
}

