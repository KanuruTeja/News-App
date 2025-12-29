package com.example.NewsApp.exception;

import com.example.NewsApp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handle(ApiException ex) {

        int statusCode = ex.getStatus().value(); 

        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiResponse<>(
                        ex.getMessage(),
                        statusCode
                ));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(
                        ex.getMessage(),
                        true
                ));
    }
}


