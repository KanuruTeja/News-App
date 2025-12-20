package com.example.NewsApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Integer roleId;
    private String city;
    private String state;
    private Double latitude;
    private Double longitude;
    private String zipCode;
    private String mobileNumber;
}
