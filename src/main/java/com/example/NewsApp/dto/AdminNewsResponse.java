package com.example.NewsApp.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminNewsResponse {

    private Long id;
    private String headline;
    private String status;
}
