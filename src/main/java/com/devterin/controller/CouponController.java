package com.devterin.controller;

import com.devterin.dtos.request.CreateCouponRequest;
import com.devterin.dtos.request.UpdateCouponRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.CouponResponse;
import com.devterin.service.impl.CouponServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupon")
public class CouponController {
    private final CouponServiceImpl couponService;

    @GetMapping("/{code}")
    public ApiResponse<CouponResponse> getCoupon(@PathVariable String code) {
        CouponResponse couponResponse = couponService.getCouponByCode(code);
        return ApiResponse.<CouponResponse>builder()
                .result(couponResponse).build();
    }

    @GetMapping
    public ApiResponse<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> coupons = couponService.getAllCoupons();
        return ApiResponse.<List<CouponResponse>>builder()
                .result(coupons).build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        CouponResponse couponResponse = couponService.createCoupon(request);
        return ApiResponse.<CouponResponse>builder()
                .message("Coupon created successfully")
                .result(couponResponse).build();
    }

    @PutMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> updateCoupon(@PathVariable Long couponId,
                                                    @Valid @RequestBody UpdateCouponRequest request) {
        CouponResponse couponResponse = couponService.updateCoupon(couponId, request);
        return ApiResponse.<CouponResponse>builder()
                .message("Coupon updated successfully")
                .result(couponResponse).build();
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ApiResponse.<Void>builder()
                .message("Coupon deleted successfully").build();
    }
}
