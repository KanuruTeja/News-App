package com.example.NewsApp.repository;

import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUser(User user);
}
