package com.devterin.controller;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.VnPayCheckoutResponse;
import com.devterin.security.CustomUserDetails;
import jakarta.validation.Valid;
import com.devterin.service.VnPayCheckoutService;
import com.devterin.service.VnPayPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment/vnpay")
public class VnPayController {
    private final VnPayCheckoutService checkoutService;
    private final VnPayPaymentService paymentService;

    @PostMapping("/checkout")
    public ApiResponse<VnPayCheckoutResponse> createCheckout(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest) {
        return ApiResponse.<VnPayCheckoutResponse>builder()
                .message("VNPay checkout created")
                .result(checkoutService.createCheckout(
                        userDetails.getId(), request, servletRequest.getRemoteAddr())).build();
    }

    @GetMapping("/orders/{orderId}/url")
    public ApiResponse<VnPayCheckoutResponse> getOrderPaymentUrl(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest) {
        String paymentUrl = paymentService.createPaymentUrlForUser(
                orderId, userDetails.getId(), servletRequest.getRemoteAddr());
        return ApiResponse.<VnPayCheckoutResponse>builder()
                .result(VnPayCheckoutResponse.builder()
                        .orderId(orderId)
                        .paymentUrl(paymentUrl)
                        .expiresAt(paymentService.getPaymentExpiry(orderId))
                        .build()).build();
    }

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> ipn(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(paymentService.processIpn(params));
    }

    @GetMapping("/return")
    public ResponseEntity<Void> paymentReturn(@RequestParam Map<String, String> params) {
        URI redirect = paymentService.createReturnRedirect(params);
        return ResponseEntity.status(302).header(HttpHeaders.LOCATION, redirect.toString()).build();
    }
}
