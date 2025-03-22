package com.devterin.dto.request;

import com.devterin.utils.TypeGender;
import com.devterin.validator.gender.GenderSubSet;
import com.devterin.validator.phone.Phone;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateUserRequest {
    @Size(min = 5, max = 50, message = "USERNAME_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "USERNAME_PATTERN_INVALID")
    @NotBlank(message = "USERNAME_NOT_BLANK_INVALID")
    private String username;

    @Size(min = 3, max = 50, message = "PASSWORD_VALID")
    private String password;

    private String fullName;
    private String email;

    @GenderSubSet(name = "gender", enumClass = TypeGender.class)
    @NotNull(message = "GENDER_NOTBLANK_INVALID")
    private String gender;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM-dd-yyyy")
    private LocalDate dob;

    @Phone
    private String phoneNumber;
    private String address;
}
