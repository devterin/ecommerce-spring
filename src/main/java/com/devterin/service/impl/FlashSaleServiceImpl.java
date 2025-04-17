package com.devterin.service.impl;

import com.devterin.dtos.request.*;
import com.devterin.dtos.response.FlashSaleItemResponse;
import com.devterin.dtos.response.FlashSaleResponse;
import com.devterin.entity.*;
import com.devterin.enums.DiscountType;
import com.devterin.enums.FlashSaleStatus;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import com.devterin.mapper.FlashSaleMapper;
import com.devterin.repository.FlashSaleItemRepository;
import com.devterin.repository.FlashSaleRepository;
import com.devterin.repository.VariantRepository;
import com.devterin.service.FlashSaleService;
import com.devterin.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {
    private final FlashSaleRepository flashSaleRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final VariantRepository variantRepository;
    private final FlashSaleMapper flashSaleMapper;
    private final OrderService orderService;

    @Override
    @Transactional
    public FlashSaleResponse createFlashSale(FlashSaleRequest request) {
        validateFlashSaleRequest(request);

        LocalDateTime endTime = request.getStartTime().plusHours(2);
        FlashSale flashSale = FlashSale.builder()
                .name(request.getFlashSaleName())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status(FlashSaleStatus.INACTIVE)
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .build();

        FlashSale savedFlashSale = flashSaleRepository.save(flashSale);

        List<FlashSaleItem> items = request.getItems().stream().map(itemRequest ->
                buildFlashSaleItem(flashSale, itemRequest, request)).collect(Collectors.toList());

        flashSaleItemRepository.saveAll(items);
        log.info("Created flash sale {} with {} items", savedFlashSale.getId(), items.size());
        return flashSaleMapper.toDto(savedFlashSale, items);
    }
    @Override
    @Transactional
    public FlashSaleResponse updateFlashSale(Long flashSaleId, FlashSaleRequest request) {
        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASH_SALE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStatus().equals(FlashSaleStatus.ACTIVE) ||
                now.isAfter(flashSale.getStartTime())) {
            throw new IllegalStateException("Cannot update flash sale that is active or has started");
        }
        flashSale.setName(request.getFlashSaleName());
        flashSale.setStartTime(request.getStartTime());
        flashSale.setEndTime(request.getStartTime().plusHours(2));
        flashSale.setDiscountType(request.getDiscountType());
        flashSale.setDiscountValue(request.getDiscountValue());

        List<FlashSaleItem> oldItems = flashSaleItemRepository.findByFlashSaleId(flashSaleId);

        List<FlashSaleItem> updatedItems = new ArrayList<>();

        for (FlashSaleItemRequest itemRequest : request.getItems()) {
            FlashSaleItem newItem = buildFlashSaleItem(flashSale, itemRequest, request);

            FlashSaleItem existingItem = oldItems.stream().filter(flashSaleItem ->
                            flashSaleItem.getVariant().getId().equals(itemRequest.getVariantId())).findFirst()
                    .orElse(null);

            if (existingItem != null) {
                existingItem.setSalePrice(newItem.getSalePrice());
                existingItem.setQuantity(newItem.getQuantity());
                updatedItems.add(existingItem);
            } else {
                updatedItems.add(newItem);
            }
        }

        List<FlashSaleItem> savedItems = flashSaleItemRepository.saveAll(updatedItems);
        return flashSaleMapper.toDto(flashSale, savedItems);
    }
    @Override
    @Transactional
    public void deleteFlashSaleItems(Long flashSaleId, List<Long> variantIds) {
        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new AppException(ErrorCode.FLASH_SALE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (flashSale.getStatus().equals(FlashSaleStatus.ACTIVE) ||
                now.isAfter(flashSale.getStartTime())) {
            throw new IllegalStateException("Cannot delete items from an active or started flash sale");
        }

        List<FlashSaleItem> itemsToDelete = flashSaleItemRepository
                .findByFlashSaleId(flashSaleId).stream()
                .filter(item -> variantIds.contains(item.getVariant().getId()))
                .filter(item -> item.getSoldQuantity() == 0) // Chỉ cho xóa nếu chưa bán
                .collect(Collectors.toList());

        if (itemsToDelete.isEmpty()) {
            throw new EntityNotFoundException("No items found with the provided variant IDs or all items have been sold.");
        }
        flashSaleItemRepository.deleteAll(itemsToDelete);
    }
    @Override
    @Transactional
    public void deactivateFlashSale(Long flashSaleId) {
        log.info("Deactivating flash sale with ID: {}", flashSaleId);

        FlashSale flashSale = flashSaleRepository.findById(flashSaleId)
                .orElseThrow(() -> new EntityNotFoundException("Flash sale not found"));
        flashSale.setStatus(FlashSaleStatus.EXPIRED);
        flashSaleRepository.save(flashSale);

        log.info("Flash sale deactivated successfully with ID: {}", flashSaleId);
    }
    @Override
    public List<FlashSaleResponse> getActiveFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSale> flashSales = flashSaleRepository.findByStatusAndStartTimeBeforeAndEndTimeAfter(
                FlashSaleStatus.ACTIVE, now, now);
        if (!flashSales.isEmpty()) {
            return flashSales.stream().map(flashSale -> {
                List<FlashSaleItem> items = flashSaleItemRepository.findAllByFlashSaleId(flashSale.getId());
                return flashSaleMapper.toDto(flashSale, items);
            }).collect(Collectors.toList());
        } else {
            throw new EntityNotFoundException("List Flash sale is empty");
        }
    }
    @Override
    @Transactional
    public FlashSaleItemResponse purchaseFlashSaleItem(Long userId, OrderRequest request) {
        FlashSaleItem item = flashSaleItemRepository.findById(request.getFlashSaleItemId())
                .orElseThrow(() -> new IllegalArgumentException("FlashSale Item not found"));

        FlashSale flashSale = item.getFlashSale();
        LocalDateTime now = LocalDateTime.now();
        if (!flashSale.getStatus().equals(FlashSaleStatus.ACTIVE) ||
                now.isBefore(flashSale.getStartTime()) ||
                now.isAfter(flashSale.getEndTime())) {
            throw new IllegalArgumentException("FlashSale not active");
        }
        orderService.createFlashSaleOrder(userId, request);

        log.info("Purchased {} units of flash sale item {}", request.getFlashSaleQuantity(), request.getFlashSaleItemId());
        return flashSaleMapper.toItemDto(item);
    }


    private void validateFlashSaleRequest(FlashSaleRequest request) {
        if (flashSaleRepository.existsByName(request.getFlashSaleName())) {
            throw new RuntimeException("FlashSale name existed");
        }

        LocalDateTime now = LocalDateTime.now();
        if (request.getStartTime().isBefore(now.plusMinutes(30))) {
            throw new IllegalArgumentException("The start time must be at least 30 minutes from the current time.");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("FlashSale must contain at least one item.");
        }
    }

    private int calculateSalePrice(int originalPrice, int discountValue, DiscountType discountType) {
        if (discountType == DiscountType.PERCENTAGE) {
            if (discountValue < 0 || discountValue > 100) {
                throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
            }
            return (int) (originalPrice * (1 - discountValue / 100.0));
        } else {
            return originalPrice - discountValue;
        }
    }

    private FlashSaleItem buildFlashSaleItem(FlashSale flashSale, FlashSaleItemRequest itemRequest, FlashSaleRequest request) {
        Variant variant = variantRepository.findById(itemRequest.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        if (variant.getStockQuantity() < itemRequest.getQuantity()) {
            throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_QUANTITY.getMessage());
        }

        int salePrice = calculateSalePrice(
                variant.getPrice(),
                request.getDiscountValue(),
                request.getDiscountType());

        if (salePrice <= 0 || salePrice > variant.getPrice()) {
            throw new IllegalArgumentException("Invalid sale price for variant: " + variant.getName());
        }

        return FlashSaleItem.builder()
                .flashSale(flashSale)
                .variant(variant)
                .salePrice(salePrice)
                .quantity(itemRequest.getQuantity())
                .soldQuantity(0)
                .build();
    }


}
