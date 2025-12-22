package com.example.NewsApp.controller;

import java.util.List;

import com.example.NewsApp.dto.ApiResponse;
import com.example.NewsApp.enums.NewsStatus;
import com.example.NewsApp.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.NewsApp.entity.News;

@RestController
@RequestMapping("/api/admin/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    // GET NEWS (ADMIN & REPORTER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<News>>> getNews(
            @RequestParam Long userId,
            @RequestParam(required = false) NewsStatus status) {

        logger.info("Entry into get news");

        List<News> newsList = newsService.getNews(userId, status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "News fetched successfully",
                        newsList
                )
        );
    }

    // ================= UPLOAD NEWS (REPORTER) =================
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<News>> uploadNews(
            @RequestParam Long userId,
            @RequestBody News news) {

        logger.info("Entry into add news");

        News savedNews = newsService.uploadNews(news, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        "News submitted successfully and waiting for admin approval",
                        savedNews
                )
        );
    }
}
