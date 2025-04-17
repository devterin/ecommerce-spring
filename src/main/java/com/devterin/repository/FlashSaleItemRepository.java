package com.devterin.repository;

import com.devterin.entity.FlashSale;
import com.devterin.entity.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    List<FlashSaleItem> findAllByFlashSaleId(Long id);
    List<FlashSaleItem> findByFlashSaleId(Long id);
}
