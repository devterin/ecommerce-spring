package com.devterin.dtos.request;

import com.devterin.utils.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UpdateCouponRequest {
    private DiscountType discountType;
    @Min(value = 1, message = "Discount value must be at least 1")
    private Integer discountValue;
    @Min(value = 0, message = "Minimum order value cannot be negative")
    private Integer minOrderValue;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime endDate;

    @Min(value = 1, message = "Max usage must be at least 1")
    private Integer maxUsage;
    @Min(value = 1, message = "Max usage per user must be at least 1")
    private Integer maxUsagePerUser;
    private Boolean isActive;
}
