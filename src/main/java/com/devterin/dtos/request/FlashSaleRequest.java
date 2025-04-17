package com.devterin.dtos.request;

import com.devterin.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FlashSaleRequest {
    @NotNull(message = "Name cannot be null")
    private String flashSaleName;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startTime;

    private DiscountType discountType;

    private Integer discountValue;

    @NotNull(message = "Items cannot be null")
    private List<FlashSaleItemRequest> items;
}
