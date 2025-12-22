package com.example.NewsApp.repository;


import com.example.NewsApp.entity.News;
import com.example.NewsApp.enums.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminNewsRepository extends JpaRepository<News, Long> {

    List<News> findByStatus(NewsStatus status);
}

