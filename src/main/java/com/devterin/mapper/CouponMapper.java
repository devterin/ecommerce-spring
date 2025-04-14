package com.devterin.mapper;

import com.devterin.dtos.response.CouponResponse;
import com.devterin.entity.Coupon;

import org.springframework.stereotype.Component;


@Component
public class CouponMapper {
    public CouponResponse toDto(Coupon coupon) {

        return CouponResponse.builder()
                .couponId(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .maxUsage(coupon.getMaxUsage())
                .currentUsage(coupon.getCurrentUsage())
                .maxUsagePerUser(coupon.getMaxUsagePerUser())
                .isActive(coupon.getActive())
                .build();
    }
}
