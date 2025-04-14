package com.devterin.entity;

import com.devterin.utils.DiscountType;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Integer discountValue; // Giá trị giảm

    private Integer minOrderValue; // Giá trị đơn hàng tối thiểu

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer maxUsage; // Số lần sử dụng tối đa (toàn cục)

    private Integer currentUsage; // Số lần đã sử dụng

    private Integer maxUsagePerUser; // Số lần sử dụng tối đa mỗi người dùng

    private Boolean active = true;

}
