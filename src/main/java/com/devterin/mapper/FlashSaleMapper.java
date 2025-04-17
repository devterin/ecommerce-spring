package com.devterin.mapper;

import com.devterin.dtos.response.FlashSaleItemResponse;
import com.devterin.dtos.response.FlashSaleResponse;
import com.devterin.entity.FlashSale;
import com.devterin.entity.FlashSaleItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlashSaleMapper {

    public FlashSaleResponse toDto(FlashSale flashSale, List<FlashSaleItem> items) {

        return FlashSaleResponse.builder()
                .flashSaleId(flashSale.getId())
                .flashSaleName(flashSale.getName())
                .startTime(flashSale.getStartTime())
                .endTime(flashSale.getEndTime())
                .status(flashSale.getStatus())
                .discountType(flashSale.getDiscountType())
                .discountValue(flashSale.getDiscountValue())
                .items(items.stream().map(this::toItemDto).collect(Collectors.toList()))
                .build();
    }


    public FlashSaleItemResponse toItemDto(FlashSaleItem item) {
        String imageUrl = item.getVariant().getImages().isEmpty() ? null
                : item.getVariant().getImages().getFirst().getImageUrl();

        return FlashSaleItemResponse.builder()
                .id(item.getId())
                .variantId(item.getVariant().getId())
                .variantName(item.getVariant().getName())
                .productName(item.getVariant().getProduct().getName())
                .imageUrl(imageUrl)
                .salePrice(item.getSalePrice())
                .quantity(item.getQuantity())
                .soldQuantity(item.getSoldQuantity())
                .build();
    }
}