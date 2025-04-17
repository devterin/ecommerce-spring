package com.devterin.entity;

import com.devterin.enums.DiscountType;
import com.devterin.enums.FlashSaleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flash_sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSale extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Integer discountValue;

    @Enumerated(EnumType.STRING)
    private FlashSaleStatus status;
}
