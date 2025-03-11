package com.devterin.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
}
