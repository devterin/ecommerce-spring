package com.devterin.controller;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.security.CustomUserDetails;
import com.devterin.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        return ApiResponse.<OrderResponse>builder()
                .message("Created order")
                .result(orderService.createOrder(userId, request))
                .build();
    }
}
