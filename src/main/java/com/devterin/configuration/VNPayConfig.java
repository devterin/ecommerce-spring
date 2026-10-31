package com.devterin.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class VNPayConfig {
    @Value("${payment.vnpay.enabled:false}")
    private boolean enabled;

    @Value("${payment.vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Value("${payment.vnpay.return-url:http://localhost:8080/api/v1/payment/vnpay/return}")
    private String returnUrl;

    @Value("${payment.vnpay.frontend-result-url:http://localhost:3000/payment/result}")
    private String frontendResultUrl;

    @Value("${payment.vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${payment.vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${payment.vnpay.order-type:other}")
    private String orderType = "other";

    @Value("${payment.vnpay.expire-minutes:15}")
    private int expireMinutes = 15;
}
