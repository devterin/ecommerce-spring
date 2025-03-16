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

    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
