package com.devterin.controller;

import com.devterin.dtos.request.CreateUserRequest;
import com.devterin.dtos.request.UpdateUserRequest;
import com.devterin.dtos.request.ChangePasswordRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.UserResponse;
import com.devterin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.devterin.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
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

    @PostMapping("/myInfo/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getId(), request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());
        return ApiResponse.<Void>builder().message("Password changed successfully").build();
    }

    @PutMapping("/myInfo")
    public ApiResponse<UserResponse> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateUserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("Updated user successfully.")
                .result(userService.updateUser(userDetails.getId(), request)).build();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId,
                                                @RequestBody @Valid UpdateUserRequest request) {

        return ApiResponse.<UserResponse>builder()
                .message("Updated user successfully.")
                .result(userService.updateUser(userId, request)).build();
    }

    @PutMapping("/myInfo/avatar")
    public ApiResponse<UserResponse> uploadMyAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("avatar") MultipartFile file) {
        return ApiResponse.<UserResponse>builder()
                .message("Avatar uploaded successfully.")
                .result(userService.uploadAvatar(userDetails.getId(), file)).build();
    }

    @PutMapping("/{userId}/avatar")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> uploadAvatar(@PathVariable Long userId,
                                                  @RequestPart("avatar") MultipartFile file) {
        return ApiResponse.<UserResponse>builder()
                .message("Avatar uploaded successfully.")
                .result(userService.uploadAvatar(userId, file)).build();
    }

    @DeleteMapping("/myInfo/avatar")
    public ApiResponse<UserResponse> deleteMyAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<UserResponse>builder()
                .message("Avatar deleted successfully.")
                .result(userService.deleteAvatar(userDetails.getId())).build();
    }

    @DeleteMapping("/{userId}/avatar")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> deleteAvatar(@PathVariable Long userId) {
        return ApiResponse.<UserResponse>builder()
                .message("Avatar deleted successfully.")
                .result(userService.deleteAvatar(userId)).build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ApiResponse.<Void>builder()
                .message("Deleted user successfully.").build();
    }

}
