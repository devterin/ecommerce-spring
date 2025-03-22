package com.devterin.controller;

import com.devterin.dto.request.IntrospectRequest;
import com.devterin.dto.request.LoginRequest;
import com.devterin.dto.request.RefreshTokenRequest;
import com.devterin.dto.response.ApiResponse;
import com.devterin.dto.response.IntrospectResponse;
import com.devterin.dto.response.LoginResponse;
import com.devterin.dto.response.RefreshTokenResponse;
import com.devterin.service.AuthService;
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
                .result(authService.authenticated(request))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authService.introspect(request))
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        var result = authService.refreshToken(request.getRefreshToken());

        return ApiResponse.<RefreshTokenResponse>builder()
                .result(result)
                .build();
    }
}
