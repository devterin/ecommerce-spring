package com.devterin.service;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getAllOrders(int pageNumber, int pageSize);
    List<OrderResponse> getOrderByUserId(Long userId);
    OrderResponse getOrderUserByOrderId(Long orderId, Long userId);
    OrderResponse createOrder(Long userId, OrderRequest request);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
    OrderResponse updateOrderDetails(Long orderId, Long userId, OrderRequest request);
    OrderResponse cancelOrder(Long orderId, Long userId);
    OrderResponse confirmOrderDeliveryByCOD(Long orderId,Long userId);
}
