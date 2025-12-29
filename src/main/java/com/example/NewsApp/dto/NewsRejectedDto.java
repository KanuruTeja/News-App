package com.example.NewsApp.dto;

import com.example.NewsApp.enums.CategoryType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsRejectedDto implements NewsView {
    private Long newsId;
    private String headline;
    private String content;
    private String rejectionReason;
    private CategoryType category;
}
