package com.devterin.controller;

import com.devterin.dtos.request.IntrospectRequest;
import com.devterin.dtos.request.LoginRequest;
import com.devterin.dtos.request.LogoutRequest;
import com.devterin.dtos.request.RefreshTokenRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.IntrospectResponse;
import com.devterin.dtos.response.LoginResponse;
import com.devterin.dtos.response.RefreshTokenResponse;
import com.devterin.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> authenticated(@RequestBody LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .message("Login successfully")
                .result(authService.authenticated(request)).build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) {
        authService.logout(request);

        return ApiResponse.<Void>builder()
                .message("Logout successfully").build();

    }


    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authService.introspect(request)).build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        var result = authService.refreshToken(request.getRefreshToken());

        return ApiResponse.<RefreshTokenResponse>builder()
                .result(result).build();
    }
}
