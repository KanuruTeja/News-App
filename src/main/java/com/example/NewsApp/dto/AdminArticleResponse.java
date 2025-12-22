package com.example.NewsApp.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminArticleResponse {

    private Long id;
    private String headline;
    private String category;
    private String newsType;
    private String reporterEmail;
    private LocalDateTime submittedAt;
}

