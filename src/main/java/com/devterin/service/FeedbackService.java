package com.devterin.service;

import com.devterin.dtos.request.FeedbackRequest;
import com.devterin.dtos.response.FeedbackResponse;
import java.util.List;

public interface FeedbackService {
    FeedbackResponse createFeedback(Long userId, Long orderId, FeedbackRequest request);
    FeedbackResponse updateFeedback(Long userId, Long orderId, FeedbackRequest request);
    List<FeedbackResponse> getProductFeedback(Long productId);
    List<FeedbackResponse> getOrderFeedback(Long userId, Long orderId);
    List<FeedbackResponse> getUserFeedback(Long userId);
}
