package com.devterin.exception;

import com.devterin.dtos.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(Exception e, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        log.error("Exception occurred: ", e);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .message(e.getMessage())
                .error(ApiResponse.ErrorDetail.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .path(request.getRequestURI())
                        .build())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<?>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ErrorCode errorCode = ErrorCode.FILE_SIZE;

        log.error("File upload failed: {}", ex.getMessage());
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .error(ApiResponse.ErrorDetail.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException e) {
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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String enumKey = e.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException i) {
            log.error(i.getMessage());
        }

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .message("Validation failed")
                .error(ApiResponse.ErrorDetail.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidJson(HttpMessageNotReadableException e) {
        log.error("Invalid JSON: {}", e.getMessage());

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(false)
                .message("JSON parse error")
                .error(ApiResponse.ErrorDetail.builder()
                        .message(e.getMessage())
                        .build())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }


}
