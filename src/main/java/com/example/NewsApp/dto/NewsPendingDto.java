package com.example.NewsApp.dto;

import java.time.LocalDateTime;

import com.example.NewsApp.enums.CategoryType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsPendingDto implements NewsView {
    private Long newsId;
    private String headline;
    private String content;
    private LocalDateTime uploadedAt;

    private String reporterName;
    private String reporterPhone;
    private CategoryType category;
}

