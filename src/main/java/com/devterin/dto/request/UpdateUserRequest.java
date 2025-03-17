package com.devterin.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateUserRequest {
    private String password;
    private String fullName;
    private String email;
    private Date dob;
    private String phoneNumber;
    private String address;
}
