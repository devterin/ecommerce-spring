package com.devterin.exception;



import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@NoArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR), //500
    USER_EXISTED(101, "User existed", HttpStatus.BAD_REQUEST), //400
    USER_NOT_FOUND(102, "User not found", HttpStatus.NOT_FOUND), //404
    USERNAME_INVALID(103, "Username must be between 5 and 50 characters", HttpStatus.BAD_REQUEST),
    USERNAME_PATTERN_INVALID(104, "Username must only contain letters and numbers, without spaces or special characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(105, "Password must be between 5 and 50 characters", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_EMPTY(108, "Username not empty", HttpStatus.BAD_REQUEST),
    INVALID_KEY(106, "Invalid message key", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
