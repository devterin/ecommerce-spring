package com.devterin.dtos.request;

import com.devterin.enums.TypeGender;
import lombok.Builder;
import lombok.Data;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private TypeGender gender;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
}
