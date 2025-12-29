package com.example.NewsApp.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.NewsApp.dto.NewsView;
import com.example.NewsApp.entity.NewsComment;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
    long countByNewsId(Long newsId);

	List<NewsComment> findByNewsIdOrderByCreatedAtDesc(Long newsId);
}
