package com.example.NewsApp.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PendingReporterResponse {

    private Long reporterId;
    private String name;
    private String email;
    private LocalDateTime registeredAt;
}
