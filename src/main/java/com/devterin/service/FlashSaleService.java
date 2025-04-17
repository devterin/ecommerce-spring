package com.devterin.service;

import com.devterin.dtos.request.FlashSaleRequest;
import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.FlashSaleItemResponse;
import com.devterin.dtos.response.FlashSaleResponse;

import java.util.List;

public interface FlashSaleService {
    FlashSaleItemResponse purchaseFlashSaleItem(Long userId, OrderRequest request);
    List<FlashSaleResponse> getActiveFlashSales();
    public void deactivateFlashSale(Long flashSaleId);
    void deleteFlashSaleItems(Long flashSaleId, List<Long> variantIds);
    FlashSaleResponse updateFlashSale(Long flashSaleId, FlashSaleRequest request);
    FlashSaleResponse createFlashSale(FlashSaleRequest request);



}
