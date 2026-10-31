package com.devterin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class VnPayExpiryScheduler {
    private final OrderService orderService;

    @Scheduled(fixedDelayString = "${payment.vnpay.expiry-scan-ms:60000}")
    public void expirePendingPayments() {
        orderService.expirePendingVnPayOrders(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }
}
