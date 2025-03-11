package com.devterin.service;

import com.devterin.dto.request.CreateUserRequest;
import com.devterin.dto.request.UpdateUserRequest;
import com.devterin.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getUsers();
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
}
