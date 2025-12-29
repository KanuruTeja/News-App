package com.example.NewsApp.repository;

import com.example.NewsApp.entity.Location;
import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.UserRole;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    void deleteByUser(User user);

	
	Optional<Location> findByUserId(Long userId);
}
