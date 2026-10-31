package com.devterin.repository;

import com.devterin.entity.FlashSale;
import com.devterin.entity.FlashSaleItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    List<FlashSaleItem> findAllByFlashSaleId(Long id);
    List<FlashSaleItem> findByFlashSaleId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT item FROM FlashSaleItem item WHERE item.id = :itemId")
    Optional<FlashSaleItem> findByIdForUpdate(@Param("itemId") Long itemId);
}
