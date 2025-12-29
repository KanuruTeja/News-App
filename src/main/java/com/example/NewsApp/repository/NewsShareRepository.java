package com.example.NewsApp.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NewsApp.dto.NewsView;
import com.example.NewsApp.entity.NewsShare;

public interface NewsShareRepository extends JpaRepository<NewsShare, Long> {
    long countByNewsId(Long newsId);

}