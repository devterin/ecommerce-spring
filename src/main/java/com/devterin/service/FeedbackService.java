package com.devterin.service;

import com.devterin.dtos.request.FeedbackRequest;
import com.devterin.dtos.response.FeedbackResponse;

public interface FeedbackService {
    FeedbackResponse createFeedback(Long userId, Long orderId, FeedbackRequest request);
    FeedbackResponse updateFeedback(Long userId, Long orderId, FeedbackRequest request);
}
