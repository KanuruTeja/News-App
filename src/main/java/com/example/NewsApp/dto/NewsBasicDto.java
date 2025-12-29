package com.example.NewsApp.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsBasicDto implements NewsView {
    private Long newsId;
    private String headline;
    private String content;
    private LocalDateTime uploadedAt;
}

