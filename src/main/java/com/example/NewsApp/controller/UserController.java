package com.example.NewsApp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.NewsApp.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        return userService.getUserById(id)
                .map(user -> {
                    response.put("message", "User found successfully");
                    response.put("data", user);
                    response.put("status", HttpStatus.OK.value());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("message", "User ID not found");
                    response.put("data", null);
                    response.put("status", HttpStatus.NOT_FOUND.value());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
    

}