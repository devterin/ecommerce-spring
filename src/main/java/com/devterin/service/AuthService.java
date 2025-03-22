package com.devterin.service;

import com.devterin.dto.request.IntrospectRequest;
import com.devterin.dto.request.LoginRequest;
import com.devterin.dto.response.IntrospectResponse;
import com.devterin.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse authenticated(LoginRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
}
