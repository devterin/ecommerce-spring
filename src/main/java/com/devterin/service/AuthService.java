package com.devterin.service;

import com.devterin.dto.request.IntrospectRequest;
import com.devterin.dto.request.LoginRequest;
import com.devterin.dto.request.RefreshTokenRequest;
import com.devterin.dto.response.IntrospectResponse;
import com.devterin.dto.response.LoginResponse;
import com.devterin.dto.response.RefreshTokenResponse;

public interface AuthService {
    LoginResponse authenticated(LoginRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    RefreshTokenResponse refreshToken(String refreshToken);
}
