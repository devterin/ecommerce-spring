package com.devterin.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private boolean success = true ;
    private String message;
    private T result;
    private ErrorDetail error;

    @Data
    @Builder
    public static class ErrorDetail {
        private int code;
        private String message;
    }

}