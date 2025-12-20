package com.example.NewsApp.exception;

public class ApiException extends RuntimeException {
    public ApiException(String msg) {
        super(msg);
    }
}

