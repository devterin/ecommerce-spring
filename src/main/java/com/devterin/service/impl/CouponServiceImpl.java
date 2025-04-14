package com.devterin.service.impl;

import com.devterin.dtos.request.CreateCouponRequest;
import com.devterin.dtos.request.UpdateCouponRequest;
import com.devterin.dtos.response.CouponResponse;
import com.devterin.entity.Coupon;
import com.devterin.entity.CouponUsage;
import com.devterin.entity.Order;
import com.devterin.entity.User;
import com.devterin.mapper.CouponMapper;
import com.devterin.repository.CouponRepository;
import com.devterin.repository.CouponUsageRepository;
import com.devterin.repository.OrderRepository;
import com.devterin.repository.UserRepository;
import com.devterin.service.CouponService;
import com.devterin.enums.DiscountType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final CouponMapper couponMapper;
    private final UserRepository userRepository;
    private final CouponUsageRepository couponUsageRepository;


    @Override
    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream()
                .map(couponMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
        return couponMapper.toDto(coupon);
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest request) {
        log.info("Creating coupon with code: {}", request.getCode());

        if (couponRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maxUsage(request.getMaxUsage())
                .currentUsage(0)
                .maxUsagePerUser(request.getMaxUsagePerUser())
                .active(true)
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);

        log.info("Coupon created successfully with ID: {}", savedCoupon.getId());
        return couponMapper.toDto(savedCoupon);
    }

    @Override
    @Transactional
    public CouponResponse updateCoupon(Long couponId, UpdateCouponRequest request) {
        log.info("Updating coupon with ID: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setStartDate(request.getStartDate());
        if (request.getEndDate().isBefore(coupon.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        coupon.setEndDate(request.getEndDate());
        coupon.setMaxUsage(request.getMaxUsage());
        coupon.setMaxUsagePerUser(request.getMaxUsagePerUser());
        coupon.setActive(request.getIsActive());

        Coupon updatedCoupon = couponRepository.save(coupon);

        log.info("Coupon updated successfully with ID: {}", updatedCoupon.getId());
        return couponMapper.toDto(updatedCoupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long couponId) {
        log.info("Deleting coupon with ID: {}", couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
        couponRepository.delete(coupon);

        log.info("Coupon deleted successfully with ID: {}", couponId);
    }

    @Override
    public void applyCoupon(String couponCode, Long userId, Order order) {
        log.info("Applying coupon: {} for userId: {}, order total: {}", couponCode, userId, order.getTotalAmount());

        Coupon coupon = couponRepository.findByCode(couponCode).orElseThrow(
                () -> new EntityNotFoundException("Coupon not found"));

        if (!coupon.getActive()) {
            throw new IllegalStateException("Coupon is not active");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            throw new IllegalStateException("Coupon is not valid at this time");
        }
        // check global usage
        if (coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new IllegalStateException("Coupon has reached its maximum usage limit");
        }
        // check min value
        if (order.getTotalAmount() < coupon.getMinOrderValue()) {
            throw new IllegalStateException("Order total is below the minimum required for this coupon");
        }
        // check user usage
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        CouponUsage usage = couponUsageRepository.findByCouponIdAndUserId(coupon.getId(), userId)
                .orElse(CouponUsage.builder()
                        .coupon(coupon)
                        .order(order)
                        .user(user)
                        .usageCount(0)
                        .build());

        if (usage.getUsageCount() >= coupon.getMaxUsagePerUser()) {
            throw new IllegalStateException("You have reached the maximum usage limit for this coupon");
        }
        // Áp dụng giảm giá
        int discountAmount;
        if (DiscountType.FIXED.equals(coupon.getDiscountType())) {
            discountAmount = coupon.getDiscountValue();
        } else {
            discountAmount = (int) (order.getTotalAmount() * coupon.getDiscountValue() / 100.0);
        }
        // save order
        order.setCoupon(coupon);
        order.setDiscountAmount(discountAmount);
        orderRepository.save(order);
        // save couponUsage
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        usage.setUsageCount(usage.getUsageCount() + 1);
        usage.setOrder(order);

        couponUsageRepository.save(usage);
        couponRepository.save(coupon);

        log.info("Coupon applied successfully: {} with discount: {}", couponCode, discountAmount);
    }

}
