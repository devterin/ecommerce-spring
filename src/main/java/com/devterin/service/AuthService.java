package com.devterin.service;

import com.devterin.dtos.request.IntrospectRequest;
import com.devterin.dtos.request.LoginRequest;
import com.devterin.dtos.request.LogoutRequest;
import com.devterin.dtos.response.IntrospectResponse;
import com.devterin.dtos.response.LoginResponse;
import com.devterin.dtos.response.RefreshTokenResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse authenticated(LoginRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    RefreshTokenResponse refreshToken(String refreshToken);
    void logout(LogoutRequest request);
}
