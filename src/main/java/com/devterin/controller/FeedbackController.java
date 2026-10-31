package com.devterin.controller;

import com.devterin.dtos.request.FeedbackRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.FeedbackResponse;
import com.devterin.security.CustomUserDetails;
import jakarta.validation.Valid;
import com.devterin.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/product/{productId}")
    ApiResponse<List<FeedbackResponse>> getProductFeedback(@PathVariable Long productId) {
        return ApiResponse.<List<FeedbackResponse>>builder().result(feedbackService.getProductFeedback(productId)).build();
    }

    @GetMapping("/order/{orderId}")
    ApiResponse<List<FeedbackResponse>> getOrderFeedback(@PathVariable Long orderId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getOrderFeedback(userDetails.getId(), orderId)).build();
    }

    @GetMapping("/my")
    ApiResponse<List<FeedbackResponse>> getMyFeedback(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getUserFeedback(userDetails.getId())).build();
    }

    @PostMapping("/{orderId}")
    ApiResponse<FeedbackResponse> feedback(@Valid @RequestBody FeedbackRequest request,
                                           @PathVariable Long orderId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        return ApiResponse.<FeedbackResponse>builder()
                .message("Feedback created successfully")
                .result(feedbackService.createFeedback(userId, orderId, request)).build();
    }

    @PutMapping("/{orderId}")
    ApiResponse<FeedbackResponse> updateFeedback(@Valid @RequestBody FeedbackRequest request,
                                                 @PathVariable Long orderId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        return ApiResponse.<FeedbackResponse>builder()
                .message("Feedback updated successfully")
                .result(feedbackService.updateFeedback(userId, orderId, request)).build();
    }

}
