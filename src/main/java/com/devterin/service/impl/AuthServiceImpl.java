package com.devterin.service.impl;

import com.devterin.dtos.request.IntrospectRequest;
import com.devterin.dtos.request.LoginRequest;
import com.devterin.dtos.request.LogoutRequest;
import com.devterin.dtos.response.IntrospectResponse;
import com.devterin.dtos.response.LoginResponse;
import com.devterin.dtos.response.RefreshTokenResponse;
import com.devterin.entity.Token;
import com.devterin.entity.User;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import com.devterin.repository.TokenRepository;
import com.devterin.repository.UserRepository;
import com.devterin.security.JwtTokenUtil;
import com.devterin.service.AuthService;
import com.devterin.service.RedisService;
import com.devterin.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final RedisService redisService;

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
            refreshToken = jwtTokenUtil.generateRefreshToken(user.getUsername());
            String refreshTokenJwtId = jwtTokenUtil.verifyRefreshToken(refreshToken).getId();

            Token token = Token.builder()
                    .id(refreshTokenJwtId)
                    .expiryTime(jwtTokenUtil.verifyRefreshToken(refreshToken).getExpiration())
                    .username(jwtTokenUtil.verifyRefreshToken(refreshToken).getSubject())
                    .build();
            tokenRepository.save(token);
        }

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        String username = jwtTokenUtil.extractUsername(request.getAccessToken());
        tokenRepository.deleteByUsername(username);

        String token = request.getAccessToken();
        String blacklistKey = "access_token:" + token;

        redisService.saveToBlacklist(blacklistKey, "blacklisted", 1, TimeUnit.HOURS);
        System.out.println("Token blacklisted key: " + blacklistKey);
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

        Claims claims = jwtTokenUtil.verifyRefreshToken(refreshToken);
        if (!tokenRepository.existsById(claims.getId())) throw new AppException(ErrorCode.INVALID_TOKEN);

        User user = userService.findByUsername(claims.getSubject());
        if (user == null) throw new AppException(ErrorCode.USER_NOT_FOUND);

        String newAccessToken = jwtTokenUtil.generateToken(user);

        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .newAccessToken(newAccessToken)
                .build();
    }


}
