package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
    private Long userId;
    private String newAccessToken;
}
