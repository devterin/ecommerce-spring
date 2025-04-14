package com.devterin.controller;

import com.devterin.dtos.request.FeedbackRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.FeedbackResponse;
import com.devterin.security.CustomUserDetails;
import com.devterin.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/{orderId}")
    ApiResponse<FeedbackResponse> feedback(@RequestBody FeedbackRequest request,
                                           @PathVariable Long orderId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        return ApiResponse.<FeedbackResponse>builder()
                .message("Feedback created successfully")
                .result(feedbackService.createFeedback(userId, orderId, request)).build();
    }

    @PutMapping("/{orderId}")
    ApiResponse<FeedbackResponse> updateFeedback(@RequestBody FeedbackRequest request,
                                                 @PathVariable Long orderId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        return ApiResponse.<FeedbackResponse>builder()
                .message("Feedback updated successfully")
                .result(feedbackService.updateFeedback(userId, orderId, request)).build();
    }

}
