package com.devterin.dtos.request;

import com.devterin.dtos.dto.OrderItemDTO;
import com.devterin.dtos.dto.PaymentDTO;
import com.devterin.utils.OrderStatus;
import com.devterin.utils.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderRequest {
    private String note;    // Ghi chú đơn hàng
    private String address;    // Địa chỉ giao hàng
    private String paymentMethod;   // Phương thức thanh toán (COD, ONLINE)
}
