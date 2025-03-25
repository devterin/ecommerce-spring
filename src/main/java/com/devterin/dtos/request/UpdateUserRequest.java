package com.devterin.dtos.request;

import lombok.Builder;
import lombok.Data;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateUserRequest {
    private String password;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
}
