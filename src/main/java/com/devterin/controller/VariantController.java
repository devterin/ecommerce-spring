package com.devterin.controller;

import com.devterin.dtos.request.VariantRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.VariantResponse;
import com.devterin.entity.Variant;
import com.devterin.service.impl.VariantServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/variant")
public class VariantController {

    private final VariantServiceImpl variantService;

    @PostMapping("/{productId}")
    public ApiResponse<VariantResponse> createVariant(@PathVariable Long productId,
                                                      @RequestBody VariantRequest request) {
        return ApiResponse.<VariantResponse>builder()
                .result(variantService.createVariant(productId, request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<VariantResponse>> getAllVariant() {
        return ApiResponse.<List<VariantResponse>>builder()
                .result(variantService.getAllVariant())
                .build();
    }

    @GetMapping("/{variantId}")
    public ApiResponse<VariantResponse> getAllVariant(@PathVariable Long variantId) {
        return ApiResponse.<VariantResponse>builder()
                .result(variantService.getVariantById(variantId))
                .build();
    }

    @PutMapping("/{variantId}")
    public ApiResponse<VariantResponse> updateVariant(@PathVariable Long variantId,
                                                      @RequestBody VariantRequest request) {
        return ApiResponse.<VariantResponse>builder()
                .result(variantService.updateVariant(variantId, request))
                .build();
    }

    @DeleteMapping("/{variantId}")
    public ApiResponse<Void> deleteVariant(@PathVariable Long variantId) {
        variantService.deleteVariant(variantId);

        return ApiResponse.<Void>builder()
                .message("Variant deleted")
                .build();
    }


}
