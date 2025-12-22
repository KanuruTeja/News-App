package com.example.NewsApp.controller;

import com.example.NewsApp.dto.AdminEditNewsRequest;
import com.example.NewsApp.dto.AdminRejectNewsRequest;
import com.example.NewsApp.dto.ApiResponse;
import com.example.NewsApp.dto.UpdateUserProfileRequest;
import com.example.NewsApp.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final AdminService adminService;

//    ============edit================
@PutMapping("/{id}/publish")
public ResponseEntity<ApiResponse<Void>> editAndPublish(
        @PathVariable Long id,
        @RequestBody AdminEditNewsRequest request) {

    logger.info("Entry into publish method");

    adminService.editAndPublish(id, request);

    return ResponseEntity.ok(
            new ApiResponse<>(
                    "News edited and published successfully",
                    null
            )
    );
}

    // ================= REJECT WITH REASON =================
    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long id,
            @RequestBody AdminRejectNewsRequest request) {

        adminService.rejectNews(id, request.getReason());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "News rejected successfully",
                        null
                )
        );
    }


    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> dashboardStats() {

        Map<String, Long> stats = adminService.getDashboardStats();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Dashboard stats fetched successfully",
                        stats
                )
        );
    }


    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @RequestBody UpdateUserProfileRequest request,
            Principal principal) {

        String email = principal.getName();

        adminService.updateProfile(request, email);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Profile updated successfully",
                        null
                )
        );
    }




//    // ================= ARTICLES =================
//    //pending
//    @GetMapping("/articles/pending")
//    public List<AdminArticleResponse> getPendingArticles() {
//        return adminService.getPendingArticles();
//    }
//
//    //verrify
//    @PutMapping("/articles/{id}/verify")
//    public void verifyArticle(@PathVariable Long id) {
//        adminService.verifyArticle(id);
//    }
//
//
//    @PutMapping("/articles/{id}/reject")
//    public void rejectArticle(
//            @PathVariable Long id,
//            @RequestBody RejectArticleRequest req) {
//        adminService.rejectArticle(id, req.getReason());
//    }
//
//    @PutMapping("/articles/{id}/publish")
//    public void publishArticle(@PathVariable Long id) {
//        adminService.publishArticle(id);
//    }
//
//    // ================= REPORTERS =================
//
//    // 1️ Get pending reporters
//    @GetMapping("/reporters/pending")
//    public List<PendingReporterResponse> getPendingReporters() {
//        return adminService.getPendingReporters();
//    }
//
//    // Approve reporter
//    @PutMapping("/reporters/{id}/approve")
//    public void approveReporter(@PathVariable Long id) {
//        adminService.approveReporter(id);
//    }
//
//    // Reject reporter
//    @PutMapping("/reporters/{id}/reject")
//    public void rejectReporter(@PathVariable Long id) {
//        adminService.rejectReporter(id);
//    }
}
