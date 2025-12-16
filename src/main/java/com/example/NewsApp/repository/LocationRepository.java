package com.example.NewsApp.repository;

import com.example.NewsApp.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {}
