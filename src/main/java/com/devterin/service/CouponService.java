package com.devterin.service;

import com.devterin.dtos.request.CreateCouponRequest;
import com.devterin.dtos.request.UpdateCouponRequest;
import com.devterin.dtos.response.CouponResponse;
import com.devterin.entity.Order;

import java.util.List;

public interface CouponService {
    List<CouponResponse> getAllCoupons();

    CouponResponse getCouponByCode(String code);

    CouponResponse createCoupon(CreateCouponRequest request);

    CouponResponse updateCoupon(Long couponId, UpdateCouponRequest request);

    void deleteCoupon(Long couponId);

    void applyCoupon(String couponCode, Long userId, Order order);
}
