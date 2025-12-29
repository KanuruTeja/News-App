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
//===================profile edit ==========
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
    @GetMapping("/get")
    public String getrest() {
    return "welcome to newsnow";
    }
}
