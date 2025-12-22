package com.example.NewsApp.service;


import java.util.List;

import com.example.NewsApp.enums.NewsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.NewsApp.entity.News;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.UserRole;
import com.example.NewsApp.repository.NewsRepository;
import com.example.NewsApp.repository.UserRepository;
import com.example.NewsApp.repository.UserRoleRepository;

@Service
public class NewsService {
    private static final Long ADMIN_ROLE_ID = 1L;
    private static final Long REPORTER_ROLE_ID = 2L;

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userrepo;

    @Autowired
    private NewsRepository newsRepository;

    public List<News> getNews(Long userId, NewsStatus status) {

        UserRole userRole = userRoleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Long roleId = userRole.getRole().getId();

        // ADMIN
        if (roleId.equals(ADMIN_ROLE_ID)) {
            if (status != null) {
                return newsRepository.findByStatus(status);
            }
            return newsRepository.findAll();
        }

        // REPORTER
        if (roleId.equals(REPORTER_ROLE_ID)) {

            User user = userrepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (status != null) {
                return newsRepository.findByCreatedByAndStatus(user, status);
            }
            return newsRepository.findByCreatedBy(user);
        }

        return List.of();
    }




    public News uploadNews(News news, Long userId) {

        // 1️⃣ Get User
        User user = userrepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Get Role
        UserRole userRole = userRoleRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User role not found"));

        Long roleId = userRole.getRole().getId();

        // 3️⃣ Map user to news
        news.setCreatedBy(user);

        // 4️⃣ Set status based on role
        if (ADMIN_ROLE_ID.equals(roleId)) {
            news.setStatus(NewsStatus.VERIFIED);
        }
        else if (REPORTER_ROLE_ID.equals(roleId)) {
            news.setStatus(NewsStatus.PENDING);
        }
        else {
            throw new RuntimeException("User not allowed to upload news");
        }

        // 5️⃣ Save
        return newsRepository.save(news);
    }

}
