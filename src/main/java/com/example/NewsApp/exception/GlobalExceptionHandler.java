package com.example.NewsApp.exception;

import com.example.NewsApp.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handle(ApiException ex) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(ex.getMessage(), true, 400, null)
        );
    }
}

