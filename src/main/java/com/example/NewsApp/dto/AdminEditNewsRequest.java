package com.example.NewsApp.dto;

import com.example.NewsApp.enums.CategoryType;
import com.example.NewsApp.enums.NewsType;
import lombok.Getter;
import lombok.Setter;

//for editing,
@Getter
@Setter
public class AdminEditNewsRequest {

    private String headline;
    private String content;
    private String mediaUrl;
    private NewsType newsType;
    private CategoryType category;
}

