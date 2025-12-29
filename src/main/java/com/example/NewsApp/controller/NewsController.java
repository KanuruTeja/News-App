package com.example.NewsApp.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.NewsApp.dto.ApiResponse;
import com.example.NewsApp.dto.NewsDetailResponse;
import com.example.NewsApp.dto.NewsResponseDto;
import com.example.NewsApp.dto.NewsView;
import com.example.NewsApp.dto.ReporterProfileResponse;
import com.example.NewsApp.entity.News;
import com.example.NewsApp.enums.DateFilter;
import com.example.NewsApp.enums.NewsStatus;
import com.example.NewsApp.service.AdminService;
import com.example.NewsApp.service.NewsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/news")
public class NewsController {

    @Autowired
    private final NewsService newsService;
    private final AdminService adminService;

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    // GET NEWS (ADMIN & REPORTER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsView>>> getNews(
            @RequestParam Long userId,
            @RequestParam(required = false) NewsStatus status,
            @RequestParam(required = false) DateFilter dateFilter,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {

        List<NewsView> response =
                newsService.getNewsWithFiltersAndStats(
                        userId, status, dateFilter, fromDate, toDate
                );

        return ResponseEntity.ok(
                new ApiResponse<>("News fetched successfully", response)
        );
    }


    // ================= UPLOAD NEWS  =================
    @PostMapping("/upload/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','REPORTER')")
    public ResponseEntity<ApiResponse<News>> uploadNews(
            @PathVariable Long userId,
            @RequestBody News news,
            Principal principal) {

        News savedNews = newsService.uploadNews(news, userId, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>("News submitted successfully", savedNews)
        );
    }


    // ================= Count =================
    @GetMapping("/stats")
//    @PreAuthorize("hasAnyRole('ADMIN','REPORTER')")
    public ResponseEntity<ApiResponse<Map<String, Map<String, Long>>>> allStats(
            @RequestParam Long userId,
            @RequestParam Integer roleId
    ) {

        Map<String, Map<String, Long>> data = Map.of(
                "total", newsService.getDashboardStats(userId, roleId, false),
                "today", newsService.getDashboardStats(userId, roleId, true)
        );

        return ResponseEntity.ok(
                new ApiResponse<>("Dashboard stats fetched", data)
        );
    }
 // ================= NEWS BY ID =================
   

        @GetMapping("/{newsId}")
        public ResponseEntity<ApiResponse<NewsDetailResponse>> getNewsDetails(
                @PathVariable Long newsId
        ) {

            NewsDetailResponse response = newsService.getNewsDetailsById(newsId);

            return ResponseEntity.ok(
                    new ApiResponse<>("News details fetched successfully", response)
            );
        }
        
        
        
      //================Get repoter by Id============
        @GetMapping("/reporterById")
        public ResponseEntity<ApiResponse<ReporterProfileResponse>> getReporter(
                @RequestParam Long userId, @RequestParam Long roleId
        ) {
            ReporterProfileResponse data = newsService.getReporterProfile(userId,roleId);
            return ResponseEntity.ok(
                    new ApiResponse<>("Reporter details fetched successfully", data)
            );
        }

        //================Get AllRepoter ============
        @GetMapping("/allReporter")
        public ResponseEntity<ApiResponse<List<ReporterProfileResponse>>> getAllReporters(
                @RequestParam Long roleId
        ) {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            "All reporter details fetched successfully",
                            newsService.getAllReporters(roleId)
                    )
            );
        }
        
        
}


