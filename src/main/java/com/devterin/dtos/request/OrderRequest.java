package com.devterin.dtos.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRequest {
    private String note;    // Ghi chú đơn hàng
    private String address;    // Địa chỉ giao hàng
    private String paymentMethod;   // Phương thức thanh toán (COD, ONLINE)
    private String couponCode;

}
