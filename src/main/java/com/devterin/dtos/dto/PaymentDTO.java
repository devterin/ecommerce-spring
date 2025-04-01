package com.devterin.dtos.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class PaymentDTO {
    private Long paymentId;
    private Integer amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDate paymentDate;
}
