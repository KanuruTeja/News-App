package com.example.NewsApp.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class NewsCommentDto {
    private Long commentId;
    private String comment;
    private Long userId;
    private String userName;
    private LocalDateTime commentedAt;
}
