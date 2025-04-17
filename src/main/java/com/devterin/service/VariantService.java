package com.devterin.service;

import com.devterin.dtos.request.VariantRequest;
import com.devterin.dtos.response.VariantResponse;
import com.devterin.entity.Variant;

import java.util.List;

public interface VariantService {
    List<VariantResponse> getAllVariant();
    VariantResponse getVariantById(Long variantId);
    Variant getVariantObjById(Long variantId);
    VariantResponse createVariant(Long productId, VariantRequest request);
    VariantResponse updateVariant(Long variantId, VariantRequest request);
    void deleteVariant(Long variantId);
}
