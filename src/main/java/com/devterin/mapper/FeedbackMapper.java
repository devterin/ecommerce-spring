package com.devterin.mapper;

import com.devterin.dtos.response.FeedbackResponse;
import com.devterin.entity.Feedback;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FeedbackMapper {

    public FeedbackResponse toDto (Feedback feedback) {
        return FeedbackResponse.builder()
                .feedbackId(feedback.getId())
                .orderId(feedback.getOrder().getId())
                .userId(feedback.getUser().getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
