package com.example.NewsApp.service;

import com.example.NewsApp.dto.AdminEditNewsRequest;
import com.example.NewsApp.dto.UpdateUserProfileRequest;
import com.example.NewsApp.entity.Location;
import com.example.NewsApp.entity.News;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.enums.NewsStatus;
import com.example.NewsApp.exception.ApiException;
import com.example.NewsApp.repository.*;
import com.example.NewsApp.serviceInterface.AdminServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements AdminServiceInterface {

    private final AdminNewsRepository adminNewsRepo;
    private final ReporterRepository reporterRepo;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    // ================= Edit news =================

    @Override
    public void editAndPublish(Long newsId, AdminEditNewsRequest req) {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() ->
                        new ApiException("News not found", HttpStatus.NOT_FOUND));

        news.setHeadline(req.getHeadline());
        news.setContent(req.getContent());
        news.setMediaUrl(req.getMediaUrl());
        news.setNewsType(req.getNewsType());
        news.setCategory(req.getCategory());

        news.setStatus(NewsStatus.PUBLISHED);
        news.setUpdatedAt(LocalDateTime.now());
        news.setPublishedAt(LocalDateTime.now());
    }

    // for count
    @Override
    public void rejectNews(Long newsId, String reason) {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() ->
                        new ApiException("News not found", HttpStatus.NOT_FOUND));

        news.setStatus(NewsStatus.REJECTED);
        news.setRejectionReason(reason);
        news.setUpdatedAt(LocalDateTime.now());
    }

    public Map<String, Long> getDashboardStats() {

        Map<String, Long> stats = new LinkedHashMap<>();

        stats.put("Total News", newsRepository.count());
        stats.put("Pending News", newsRepository.countByStatus(NewsStatus.PENDING));
        stats.put("Published News", newsRepository.countByStatus(NewsStatus.PUBLISHED));
        stats.put("Rejected News", newsRepository.countByStatus(NewsStatus.REJECTED));
        stats.put("Reporter Management", reporterRepo.count());

        return stats;
    }

    // profile uodate

    public void updateProfile(UpdateUserProfileRequest req, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ApiException("User not found", HttpStatus.NOT_FOUND));

        // ===== Update User table =====
        if (req.getName() != null) {
            user.setName(req.getName());
        }

        if (req.getMobileNumber() != null) {
            user.setMobileNumber(req.getMobileNumber());
        }

        user.setRegisteredAt(
                user.getRegisteredAt() == null
                        ? LocalDateTime.now()
                        : user.getRegisteredAt()
        );

        // ===== Update Location table =====
        Location location = user.getLocation();

        if (location == null) {
            location = new Location();
            location.setUser(user);
        }

        if (req.getCity() != null) location.setCity(req.getCity());
        if (req.getState() != null) location.setState(req.getState());
        if (req.getZipCode() != null) location.setZipCode(req.getZipCode());
        if (req.getLatitude() != null) location.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) location.setLongitude(req.getLongitude());

        locationRepository.save(location);
        userRepository.save(user);
    }



//    // ================= ARTICLES =================
//
//    @Override
//    public List<AdminArticleResponse> getPendingArticles() {
//        return adminNewsRepo.findByStatus(NewsStatus.PENDING)
//                .stream()
//                .map(this::mapToDto)
//                .toList();
//    }
//
//    @Override
//    public void verifyArticle(Long articleId) {
//        News news = getArticle(articleId);
//        news.setStatus(NewsStatus.VERIFIED);
//        news.setUpdatedAt(LocalDateTime.now());
//    }
//
//    @Override
//    public void rejectArticle(Long articleId, String reason) {
//        News news = getArticle(articleId);
//        news.setStatus(NewsStatus.REJECTED);
//        news.setUpdatedAt(LocalDateTime.now());
//    }
//
//    @Override
//    public void publishArticle(Long articleId) {
//        News news = getArticle(articleId);
//        news.setStatus(NewsStatus.PUBLISHED);
//        news.setPublishedAt(LocalDateTime.now());
//    }
//
//    private News getArticle(Long id) {
//        return adminNewsRepo.findById(id)
//                .orElseThrow(() ->
//                        new ApiException("Article not found", HttpStatus.NOT_FOUND)
//                );
//    }
//
//    private AdminArticleResponse mapToDto(News news) {
//        return AdminArticleResponse.builder()
//                .id(news.getId())
//                .headline(news.getHeadline())
//                .category(news.getCategory().name())
//                .newsType(news.getNewsType().name())
//                .reporterEmail(news.getReporter().getEmail())
//                .submittedAt(news.getCreatedAt())
//                .build();
//    }
//
//    // ================= REPORTERS =================
//
//    @Override
//    public List<PendingReporterResponse> getPendingReporters() {
//
//        return reporterRepo.findByStatus(ReporterStatus.PENDING)
//                .stream()
//                .map(r -> PendingReporterResponse.builder()
//                        .reporterId(r.getId())
//                        .name(r.getUser().getName())
//                        .email(r.getUser().getEmail())
//                        .registeredAt(r.getRegisteredAt())
//                        .build())
//                .toList();
//    }
//
//    @Override
//    public void approveReporter(Long reporterId) {
//
//        Reporter reporter = getReporter(reporterId);
//        reporter.setStatus(ReporterStatus.APPROVED);
//        reporter.setApprovedAt(LocalDateTime.now());
//    }
//
//    @Override
//    public void rejectReporter(Long reporterId) {
//
//        Reporter reporter = getReporter(reporterId);
//        reporter.setStatus(ReporterStatus.REJECTED);
//    }
//
//    private Reporter getReporter(Long id) {
//        return reporterRepo.findById(id)
//                .orElseThrow(() ->
//                        new ApiException("Reporter not found", HttpStatus.NOT_FOUND)
//                );
//    }
}
