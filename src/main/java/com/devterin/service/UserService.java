package com.devterin.service;

import com.devterin.dtos.request.CreateUserRequest;
import com.devterin.dtos.request.UpdateUserRequest;
import com.devterin.dtos.response.UserResponse;
import com.devterin.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getUsers();
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
    User findByUsername(String username);
}
