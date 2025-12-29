package com.example.NewsApp.dto;

import com.example.NewsApp.enums.CategoryType;
import com.example.NewsApp.enums.NewsType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsPublishedDto implements NewsView {
    private Long newsId;
    private CategoryType category;
    private String headline;
    private String content;

    private long commentCount;
    private long likeCount;
    private long shareCount;
    private long saveCount;

    private NewsType newsType;
}
