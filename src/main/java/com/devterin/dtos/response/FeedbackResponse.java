package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class FeedbackResponse {
    private Long feedbackId;
    private Long userId;
    private String userName;
    private Long orderId;
    private Long productId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
