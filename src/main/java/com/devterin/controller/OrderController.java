package com.devterin.controller;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.security.CustomUserDetails;
import com.devterin.enums.OrderStatus;
import com.devterin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ApiResponse<List<OrderResponse>> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {

        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders(pageNumber, pageSize)).build();
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        return ApiResponse.<OrderResponse>builder()
                .message("Created order")
                .result(orderService.createOrder(userId, request)).build();
    }

    @PatchMapping("/{orderId}")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                        @RequestParam("status") String status) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, OrderStatus.valueOf(status))).build();
    }

    @GetMapping("/user")
    public ApiResponse<List<OrderResponse>> getOrderByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getOrderByUserId(userId)).build();
    }

    @GetMapping("/user/{orderId}")
    public ApiResponse<OrderResponse> getOrderByUser(@PathVariable Long orderId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderUserByOrderId(orderId, userId)).build();
    }

    @PostMapping("/user/cancel/{orderId}")
    public ApiResponse<OrderResponse> cancelOrders(@PathVariable Long orderId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        return ApiResponse.<OrderResponse>builder()
                .message("Order cancelled successfully")
                .result(orderService.cancelOrder(orderId, userId)).build();
    }

    @PatchMapping("/user/{orderId}")
    public ApiResponse<OrderResponse> updateOrderDetails(@PathVariable Long orderId,
                                                         @RequestBody OrderRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderDetails(orderId, userDetails.getId(), request)).build();
    }

    @PostMapping("/user/confirm/{orderId}")
    public ApiResponse<OrderResponse> confirmOrder(@PathVariable Long orderId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.confirmOrderDeliveryByCOD(orderId, userId)).build();
    }


}
