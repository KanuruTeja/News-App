package com.example.NewsApp.serviceInterface;

import com.example.NewsApp.dto.AdminEditNewsRequest;
import com.example.NewsApp.dto.PendingReporterResponse;

import java.util.List;

public interface AdminServiceInterface {

//========================edit======================
    void editAndPublish(Long newsId, AdminEditNewsRequest request);

    void rejectNews(Long newsId, String reason);






//    // ===== Articles =====
////    List<AdminArticleResponse> getPendingArticles();
//
//    void verifyArticle(Long articleId);
//
////    void rejectArticle(Long articleId, String reason);
//
//    void publishArticle(Long articleId);
//
//
//
//    // ===== Reporters =====
//    List<PendingReporterResponse> getPendingReporters();
//
//    void approveReporter(Long reporterId);
//
//    void rejectReporter(Long reporterId);
}


