package com.example.NewsApp.repository;

import com.example.NewsApp.entity.Reporter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReporterRepository extends JpaRepository<Reporter, Long> {
    long countByRegisteredAtBetween(LocalDateTime start, LocalDateTime end);
}


