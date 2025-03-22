package com.devterin.service.impl;

import com.devterin.dto.request.IntrospectRequest;
import com.devterin.dto.request.LoginRequest;
import com.devterin.dto.response.IntrospectResponse;
import com.devterin.dto.response.LoginResponse;
import com.devterin.dto.response.RefreshTokenResponse;
import com.devterin.entity.User;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import com.devterin.repository.UserRepository;
import com.devterin.security.JwtTokenUtil;
import com.devterin.service.AuthService;
import com.devterin.service.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    @Override
    public LoginResponse authenticated(LoginRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }

        String accessToken = null;
        String refreshToken = null;

        if (auth.isAuthenticated()) {
            accessToken = jwtTokenUtil.generateToken(user);
            refreshToken = jwtTokenUtil.generateRefreshToken(user);
        }

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;

        try {
            jwtTokenUtil.extractAllClaims(token);
        } catch (JwtException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {

        if (refreshToken == null) {
            throw new RuntimeException("Token must be not null");
        }

        String username = jwtTokenUtil.verifyRefreshToken(refreshToken);
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        String newAccessToken  = jwtTokenUtil.generateToken(user);

        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .refreshToken(newAccessToken)
                .build();
    }


}
