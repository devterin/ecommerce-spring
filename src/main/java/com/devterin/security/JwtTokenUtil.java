package com.devterin.security;

import com.devterin.entity.User;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParseException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtTokenUtil {

    @NonFinal
    @Value("${jwt.secretKey.accessToken}")
    private String ACCESS_KEY;

    @NonFinal
    @Value("${jwt.secretKey.refreshToken}")
    private String REFRESH_KEY;

    @NonFinal
    @Value("${jwt.expire.accessToken}")
    private long ACCESS_EXPIRATION;

    @NonFinal
    @Value("${jwt.expire.refreshToken}")
    private long REFRESH_EXPIRATION;

    // decode and get the key
    public SecretKey getSecretKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generate token using jwt utility class and return token as string
    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", getRoles(user))
                .id(UUID.randomUUID().toString())
                .issuer("devterin")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getSecretKey(ACCESS_KEY))
                .compact();
    }
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getSecretKey(REFRESH_KEY))
                .compact();
    }

    private String getRoles(UserDetails userDetails) {

        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

//        private String getRoles(User user) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if (!CollectionUtils.isEmpty(user.getRoles())) {
//            user.getRoles().forEach(role -> {
//                stringJoiner.add("ROLE_" + role.getName());
//            });
//        }
//        return stringJoiner.toString();
//    }
    public boolean validateToken(String token, UserDetails userDetails) {

        final String userName = extractUsername(token);

        Jwts.parser()
                .verifyWith(getSecretKey(ACCESS_KEY))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey(ACCESS_KEY))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Claims verifyRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey(REFRESH_KEY))
                .build()
                .parseSignedClaims(token)
                .getPayload();
//                .getSubject();
    }

}
