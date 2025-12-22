package com.example.NewsApp.repository;

import com.example.NewsApp.entity.User;
import com.example.NewsApp.entity.UserRole;
import com.example.NewsApp.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    Optional<UserRole> findByUser(User user);
    void deleteByUser(User user);
    Optional<UserRole> findByUserId(Long userId);

}
