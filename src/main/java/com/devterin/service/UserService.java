package com.devterin.service;

import com.devterin.dtos.request.CreateUserRequest;
import com.devterin.dtos.request.UpdateUserRequest;
import com.devterin.dtos.response.UserResponse;
import com.devterin.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getAllUsers();
    List<UserResponse> getAllUsers(int pageNumber, int pageSize);
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
    User findByUsername(String username);
}
