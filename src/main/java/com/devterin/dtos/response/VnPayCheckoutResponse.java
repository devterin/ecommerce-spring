package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VnPayCheckoutResponse {
    private Long orderId;
    private String paymentUrl;
    private LocalDateTime expiresAt;
}
