package com.devterin.dtos.response;

import com.devterin.enums.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private Long couponId;
    private String code;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxUsage;
    private Integer currentUsage;
    private Integer maxUsagePerUser;
    private Boolean isActive;
}
