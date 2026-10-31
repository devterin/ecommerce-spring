package com.devterin.service.impl;

import com.devterin.dtos.request.FeedbackRequest;
import com.devterin.dtos.response.FeedbackResponse;
import com.devterin.entity.Feedback;
import com.devterin.entity.Order;
import com.devterin.entity.OrderItem;
import com.devterin.entity.Product;
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
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getProductFeedback(Long productId) {
        return feedbackRepository.findAllProductFeedback(productId).stream().map(feedbackMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getOrderFeedback(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to view feedback for this order");
        }
        return feedbackRepository.findAllByOrderIdAndUserIdOrderByCreatedAtDesc(orderId, userId)
                .stream().map(feedbackMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getUserFeedback(Long userId) {
        return feedbackRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(feedbackMapper::toDto).toList();
    }

    @Override
    @Transactional
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
        Long productId = request.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException("Product is required for a review");
        }
        Product purchasedProduct = order.getOrderItems().stream()
                .map(OrderItem::getVariant)
                .filter(variant -> variant.getProduct().getId().equals(productId))
                .map(variant -> variant.getProduct())
                .findFirst()
                .orElseThrow(() -> new SecurityException("This product was not purchased in the order"));
        if (feedbackRepository.existsByOrderIdAndUserIdAndProductId(orderId, userId, productId)) {
            throw new IllegalStateException("This product has already been reviewed for this order");
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .order(order)
                .product(purchasedProduct)
                .rating(request.getRating())
                .comment(request.getComment())
                .editCount(0)
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);

        log.info("Feedback created successfully with ID: {}", savedFeedback.getId());
        return feedbackMapper.toDto(savedFeedback);
    }

    @Override
    @Transactional
    public FeedbackResponse updateFeedback(Long userId, Long orderId, FeedbackRequest request) {

        Long productId = request.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException("Product is required for a review");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to update feedback for this order");
        }
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Feedback can only be updated for delivered orders");
        }
        order.getOrderItems().stream()
                .map(OrderItem::getVariant)
                .filter(variant -> variant.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new SecurityException("This product was not purchased in the order"));

        Feedback feedback = feedbackRepository.findByOrderIdAndUserIdAndProductId(orderId, userId, productId);
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
