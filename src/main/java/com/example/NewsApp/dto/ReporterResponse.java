package com.example.NewsApp.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReporterResponse {

    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private LocalDateTime registeredAt;
}

