package com.example.NewsApp.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private String email;
    private String status;
    private LocalDateTime createdAt;
}

