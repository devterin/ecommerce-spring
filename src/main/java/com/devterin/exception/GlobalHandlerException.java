package com.devterin.exception;

import com.devterin.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(Exception e, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        log.error(e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .error(ApiResponse.ErrorDetail.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<?>> handlerException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error(e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .error(ApiResponse.ErrorDetail.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

}
