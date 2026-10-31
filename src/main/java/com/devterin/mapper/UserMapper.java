package com.devterin.mapper;

import com.devterin.dtos.request.CreateUserRequest;
import com.devterin.dtos.response.UserResponse;
import com.devterin.entity.User;
import com.devterin.util.AppConstants;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponse toDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatar() != null ? user.getAvatar() : AppConstants.URL_DEFAULT_AVATAR)
                .typeGender(user.getGender())
                .dob(user.getDob())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .roles(user.getRoles() == null ? Set.of() : user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();
    }

    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .gender(request.getGender())
                .dob(request.getDob())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();
    }

}
