package com.devterin.controller;

import com.devterin.dtos.request.DeliveryAddressRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.DeliveryAddressResponse;
import com.devterin.security.CustomUserDetails;
import com.devterin.service.DeliveryAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/myInfo/addresses")
public class DeliveryAddressController {
    private final DeliveryAddressService addressService;

    @GetMapping
    public ApiResponse<List<DeliveryAddressResponse>> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.<List<DeliveryAddressResponse>>builder()
                .message("Delivery addresses")
                .result(addressService.getMyAddresses(userDetails.getId()))
                .build();
    }

    @PostMapping
    public ApiResponse<DeliveryAddressResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DeliveryAddressRequest request) {
        return ApiResponse.<DeliveryAddressResponse>builder()
                .message("Delivery address created")
                .result(addressService.create(userDetails.getId(), request))
                .build();
    }

    @PutMapping("/{addressId}")
    public ApiResponse<DeliveryAddressResponse> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId,
            @Valid @RequestBody DeliveryAddressRequest request) {
        return ApiResponse.<DeliveryAddressResponse>builder()
                .message("Delivery address updated")
                .result(addressService.update(userDetails.getId(), addressId, request))
                .build();
    }

    @PutMapping("/{addressId}/default")
    public ApiResponse<DeliveryAddressResponse> setDefault(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        return ApiResponse.<DeliveryAddressResponse>builder()
                .message("Default delivery address updated")
                .result(addressService.setDefault(userDetails.getId(), addressId))
                .build();
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        addressService.delete(userDetails.getId(), addressId);
        return ApiResponse.<Void>builder().message("Delivery address deleted").build();
    }
}
