package com.devterin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.*;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateUserRequest {
    @Size(min = 5, max = 50, message = "USERNAME_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "USERNAME_PATTERN_INVALID")
    @NotBlank(message = "USERNAME_NOT_EMPTY")
    private String username;
    @Size(min = 3, max = 50, message = "PASSWORD_VALID")
    private String password;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
}
