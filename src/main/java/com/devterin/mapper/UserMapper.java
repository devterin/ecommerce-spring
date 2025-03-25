package com.devterin.mapper;

import com.devterin.dtos.request.CreateUserRequest;
import com.devterin.dtos.response.UserResponse;
import com.devterin.entity.User;
import com.devterin.utils.TypeGender;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserResponse toDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .typeGender(user.getGender())
                .dob(user.getDob())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .gender(TypeGender.valueOf(request.getGender()))
                .dob(request.getDob())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();
    }

}
