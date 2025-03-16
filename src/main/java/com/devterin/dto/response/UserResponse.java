package com.devterin.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.*;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String phoneNumber;
    private String address;
}
