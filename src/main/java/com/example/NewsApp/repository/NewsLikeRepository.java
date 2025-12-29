package com.example.NewsApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NewsApp.entity.NewsLike;

public interface NewsLikeRepository extends JpaRepository<NewsLike, Long> {
    long countByNewsId(Long newsId);
}
