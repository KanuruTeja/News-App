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

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements AdminServiceInterface {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    // ================= Edit news =================

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
    public void rejectNews(Long newsId, String reason) {

        News news = newsRepository.findById(newsId)
                .orElseThrow(() ->
                        new ApiException("News not found", HttpStatus.NOT_FOUND));

        news.setStatus(NewsStatus.REJECTED);
        news.setRejectionReason(reason);
        news.setUpdatedAt(LocalDateTime.now());
    }


    // profile update
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

}
