package com.example.NewsApp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.NewsApp.enums.CategoryType;
import com.example.NewsApp.enums.NewsType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsDetailResponse {

    private Long newsId;
    private String headline;
    private String content;

    private CategoryType category;
    private NewsType newsType;

    private String mediaUrl;   // image / audio / video

    private LocalDateTime uploadedAt;

    // Reporter details
    private Long reporterId;
    private String reporterName;
    private String reporterEmail;
    private String reporterPhone;

    // Location
    private String district;
    private String city;
    private String state;

    // Counts
    private long likeCount;
    private long commentCount;
    private long shareCount;
    private long saveCount;

    // Comments
    private List<NewsCommentDto> comments;
}

