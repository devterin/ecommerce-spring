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
    USERNAME_NOT_BLANK_INVALID(105, "Username must be not blank", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(106, "Password must be between 5 and 50 characters", HttpStatus.BAD_REQUEST),
    INVALID_KEY(107, "Invalid message key", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(108, "Invalid phone number", HttpStatus.BAD_REQUEST),
    DOB_INVALID(109, "Invalid date of birth", HttpStatus.BAD_REQUEST),
    GENDER_INVALID(110, "Invalid gender", HttpStatus.BAD_REQUEST),
    GENDER_NOT_BLANK_INVALID(111, "Gender must be not blank", HttpStatus.BAD_REQUEST),
    LOGIN_FAILED(111, "Username or password incorrect", HttpStatus.UNAUTHORIZED),
    USER_NOT_EXISTED(112, "User not existed", HttpStatus.BAD_REQUEST), //400
    INVALID_TOKEN(113, "Invalid Access Token", HttpStatus.BAD_REQUEST), //400
    TOKEN_EXPIRED(114, "Access token expired", HttpStatus.UNAUTHORIZED), //401
    JSON_INVALID(115, "Json invalid", HttpStatus.BAD_REQUEST), //401
    FILE_SIZE(116, "File size exceeds limit of 5MB", HttpStatus.BAD_REQUEST), //401
    REQUEST_SIZE(117, "Request size exceeds limit of 20MB", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(118, "Product not found", HttpStatus.BAD_REQUEST),
    VARIANT_NOT_FOUND(119, "Variant not found", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_QUANTITY(120, "Insufficient quantity", HttpStatus.BAD_REQUEST),
    INVALID_TIME_RANGE(120, "Invalid time range", HttpStatus.BAD_REQUEST),

    FLASH_SALE_NOT_FOUND(121, "Flash sale not found" ,HttpStatus.BAD_REQUEST );

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
