package com.devterin.controller;

import com.devterin.dtos.request.CreateUserRequest;
import com.devterin.dtos.request.UpdateUserRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.UserResponse;
import com.devterin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestPart(value = "avatar", required = false) MultipartFile file,
                                                @Valid @RequestPart("request") CreateUserRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message("Created user successfully.")
                .result(userService.createUser(request, file)).build();
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + authentication.getName());
        System.out.println("Roles: " + authentication.getAuthorities());

        return ApiResponse.<List<UserResponse>>builder()
                .message("Success")
                .result(userService.getAllUsers(pageNumber, pageSize)).build();
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {

        return ApiResponse.<UserResponse>builder()
                .message("Personal information")
                .result(userService.getMyInfo()).build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId,
                                                @RequestPart("request") UpdateUserRequest request,
                                                @RequestPart(value = "avatar", required = false) MultipartFile file) {


        return ApiResponse.<UserResponse>builder()
                .message("Updated user successfully.")
                .result(userService.updateUser(userId, request, file)).build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ApiResponse.<Void>builder()
                .message("Deleted user successfully.").build();
    }

}
