package com.example.NewsApp.repository;

import com.example.NewsApp.entity.News;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.enums.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    // ADMIN use
    List<News> findByStatus(NewsStatus status);

    // REPORTER use (by User entity)
    List<News> findByCreatedBy(User user);

    List<News> findByCreatedByAndStatus(User user, NewsStatus status);


    List<News> findByCreatedBy_Id(Long userId);

    List<News> findByCreatedBy_IdAndStatus(Long userId, NewsStatus status);

    Optional<News> findFirstByCreatedBy_IdAndStatusOrderByCreatedAtDesc(
            Long userId,
            NewsStatus status
    );


    //for count
    long countByStatus(NewsStatus status);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreatedAtBetween(
            NewsStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByCreatedBy_Id(Long userId);

    long countByCreatedBy_IdAndStatus(Long userId, NewsStatus status);

    long countByCreatedBy_IdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByCreatedBy_IdAndStatusAndCreatedAtBetween(
            Long userId,
            NewsStatus status,
            LocalDateTime start,
            LocalDateTime end
    );


    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, NewsStatus status);

    List<News> findByUserId(Long userId);

	List<News> findByStatusAndCreatedAtBetween(NewsStatus status, LocalDateTime start, LocalDateTime end);

	List<News> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	List<News> findByCreatedByAndStatusAndCreatedAtBetween(User user, NewsStatus status, LocalDateTime start,
			LocalDateTime end);

	List<News> findByCreatedByAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);


}

