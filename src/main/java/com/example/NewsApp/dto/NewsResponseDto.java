package com.example.NewsApp.dto;

import java.time.LocalDateTime;

import com.example.NewsApp.enums.CategoryType;
import com.example.NewsApp.enums.NewsStatus;
import com.example.NewsApp.enums.NewsType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsResponseDto {

    private Long newsId;
    private String headline;
    private String content;
    private CategoryType category;
    private NewsType newsType;
    private NewsStatus status;

    private LocalDateTime publishedAt;
    private LocalDateTime uploadedAt;

    private boolean verified;

    private String reporterName;
    private String reporterPhone;

    private String rejectionReason;

    private long likeCount;
    private long commentCount;
    private long shareCount;
}

