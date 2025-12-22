package com.example.NewsApp.repository;


import com.example.NewsApp.entity.News;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.enums.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    // ADMIN use
    List<News> findByStatus(NewsStatus status);


    //for count
    long countByStatus(NewsStatus status);

    // REPORTER use (by User entity)
    List<News> findByCreatedBy(User user);

    List<News> findByCreatedByAndStatus(User user, NewsStatus status);

    // OPTIONAL: if you want to pass userId directly
    List<News> findByCreatedBy_Id(Long userId);

    List<News> findByCreatedBy_IdAndStatus(Long userId, NewsStatus status);

    Optional<News> findFirstByCreatedBy_IdAndStatusOrderByCreatedAtDesc(
            Long userId,
            NewsStatus status
    );

}

