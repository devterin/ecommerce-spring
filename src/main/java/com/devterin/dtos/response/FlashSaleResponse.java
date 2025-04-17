package com.devterin.dtos.response;

import com.devterin.enums.DiscountType;
import com.devterin.enums.FlashSaleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class FlashSaleResponse {
    private Long flashSaleId;
    private String flashSaleName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private FlashSaleStatus status;
    private DiscountType discountType;
    private Integer discountValue;
    private List<FlashSaleItemResponse> items;
}
