package com.devterin.controller;

import com.devterin.dtos.request.FlashSaleRequest;
import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.ApiResponse;
import com.devterin.dtos.response.FlashSaleItemResponse;
import com.devterin.dtos.response.FlashSaleResponse;
import com.devterin.security.CustomUserDetails;
import com.devterin.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fs")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    @PostMapping
    public ApiResponse<FlashSaleResponse> createFlashSale(@RequestBody FlashSaleRequest request) {

        return ApiResponse.<FlashSaleResponse>builder()
                .message("Created flash_sale")
                .result(flashSaleService.createFlashSale(request)).build();
    }

    @PutMapping("/{flashSaleId}")
    public ApiResponse<FlashSaleResponse> updateFlashSale(@PathVariable Long flashSaleId,
                                                          @RequestBody FlashSaleRequest request) {

        return ApiResponse.<FlashSaleResponse>builder()
                .message("Updated flash_sale")
                .result(flashSaleService.updateFlashSale(flashSaleId, request)).build();
    }

    @DeleteMapping("/{flashSaleId}")
    public ApiResponse<Void> deleteFlashSaleItems(@PathVariable Long flashSaleId,
                                                  @RequestBody List<Long> variantIds) {
        flashSaleService.deleteFlashSaleItems(flashSaleId, variantIds);
        return ApiResponse.<Void>builder()
                .message("Deleted item").build();
    }

    @PostMapping("/purchase")
    public ApiResponse<FlashSaleItemResponse> purchaseFlashSaleItem(@RequestBody OrderRequest request,
                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.<FlashSaleItemResponse>builder()
                .message("Purchased item flash sale")
                .result(flashSaleService.purchaseFlashSaleItem(userDetails.getId(), request)).build();
    }


    @GetMapping("/isActive")
    public ApiResponse<List<FlashSaleResponse>> getAllFlashSalIsActive() {

        return ApiResponse.<List<FlashSaleResponse>>builder()
                .result(flashSaleService.getActiveFlashSales()).build();
    }

    @GetMapping("/deactivate/{fsId}")
    public ApiResponse<Void> deactivateFlashSale(@PathVariable Long fsId) {
        flashSaleService.deactivateFlashSale(fsId);

        return ApiResponse.<Void>builder()
                .message("Deactivated flash sale id: " + fsId).build();
    }
}
