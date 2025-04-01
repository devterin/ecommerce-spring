package com.devterin.utils;

public enum OrderStatus {
    PENDING,     // Đơn hàng đang chờ xử lý
    PROCESSING, // Đơn hàng đang được xử lý
    SHIPPED,     // Đơn hàng đã được gửi đi
    DELIVERED,   // Đơn hàng đã giao thành công
    CANCELLED,   // Đơn hàng đã bị hủy
}
