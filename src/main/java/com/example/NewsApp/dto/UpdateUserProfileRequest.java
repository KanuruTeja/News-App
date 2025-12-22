package com.example.NewsApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequest {

    private String name;
    private String mobileNumber;

    private String city;
    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;
}

