package com.devterin.service;

import com.devterin.dto.request.CreateUserRequest;
import com.devterin.dto.request.UpdateUserRequest;
import com.devterin.dto.response.UserResponse;
import com.devterin.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getUsers();
    UserResponse updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
    User findByUsername(String username);
}
