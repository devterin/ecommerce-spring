//package com.devterin.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.security.core.Transient;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Builder
////@Entity
////@Table(name = "token")
//@Transient
//public class Token {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "token", nullable = false, unique = true)
//    private String token;
//
//    @Column(name = "refresh_token", nullable = false)
//    private String refreshToken;
//
//    @Column(name = "token_type", nullable = false, length = 50)
//    private String tokenType;
//
//    @Column(name = "expired", nullable = false)
//    private boolean expired;
//
//}