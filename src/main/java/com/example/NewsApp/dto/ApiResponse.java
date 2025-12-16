package com.example.NewsApp.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private boolean error;
    private int code;
    private T data;
}
