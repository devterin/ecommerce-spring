package com.devterin.service.impl;

import com.devterin.dtos.request.FeedbackRequest;
import com.devterin.dtos.response.FeedbackResponse;
import com.devterin.entity.Feedback;
import com.devterin.entity.Order;
import com.devterin.entity.User;
import com.devterin.mapper.FeedbackMapper;
import com.devterin.repository.FeedbackRepository;
import com.devterin.repository.OrderRepository;
import com.devterin.repository.UserRepository;
import com.devterin.service.FeedbackService;
import com.devterin.enums.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    public FeedbackResponse createFeedback(Long userId, Long orderId, FeedbackRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to provide feedback for this order");
        }
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Feedback can only be provided for delivered orders");
        }
        if (feedbackRepository.existsByOrderIdAndUserId(orderId, userId)) {
            throw new IllegalStateException("Feedback already exists for this order");
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .editCount(0)
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);

        log.info("Feedback created successfully with ID: {}", savedFeedback.getId());
        return feedbackMapper.toDto(savedFeedback);
    }

    @Override
    public FeedbackResponse updateFeedback(Long userId, Long orderId, FeedbackRequest request) {

        Feedback feedback = feedbackRepository.findByOrderIdAndUserId(orderId, userId);
        if (feedback == null) {
            throw new EntityNotFoundException("Feedback not found for orderId: " + orderId);
        }
        if (feedback.getEditCount() >= 2) {
            throw new IllegalStateException("Feedback can only edit up to 2 times");
        }

        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setEditCount(feedback.getEditCount() + 1);

        Feedback saveFeedback = feedbackRepository.save(feedback);
        log.info("Feedback updated editCount={}", saveFeedback.getEditCount());

        return feedbackMapper.toDto(saveFeedback);
    }

}
