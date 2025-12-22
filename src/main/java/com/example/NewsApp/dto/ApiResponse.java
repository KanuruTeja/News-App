package com.example.NewsApp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // hides null fields
public class ApiResponse<T> {

    private String message;
    private boolean error;
    private Integer code;
    private T data;

    // ✅ SUCCESS RESPONSE
    public ApiResponse(String message, T data) {
        this.message = message;
        this.error = false;
        this.data = data;
        this.code = null;
    }

    // ✅ ERROR RESPONSE
    public ApiResponse(String message, int code) {
        this.message = message;
        this.error = true;
        this.code = code;
        this.data = null;
    }

    // ✅ INTERNAL / FLEXIBLE (used by controller if needed)
    public ApiResponse(String message, boolean error, Integer code, T data) {
        this.message = message;
        this.error = error;
        this.code = code;
        this.data = data;
    }

    
}
