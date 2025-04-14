package com.devterin.dtos.request;

import com.devterin.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateCouponRequest {
    @NotBlank(message = "Coupon code is required")
    private String code;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Min(value = 1, message = "Discount value must be at least 1")
    private Integer discountValue;

    @NotNull(message = "Minimum order value is required")
    @Min(value = 0, message = "Minimum order value cannot be negative")
    private Integer minOrderValue;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Max usage is required")
    @Min(value = 1, message = "Max usage must be at least 1")
    private Integer maxUsage;

    @NotNull(message = "Max usage per user is required")
    @Min(value = 1, message = "Max usage per user must be at least 1")
    private Integer maxUsagePerUser;
}
