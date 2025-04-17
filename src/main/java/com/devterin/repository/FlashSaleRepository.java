package com.devterin.repository;

import com.devterin.entity.FlashSale;
import com.devterin.enums.FlashSaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    List<FlashSale> findByStatusAndStartTimeBeforeAndEndTimeAfter(
            FlashSaleStatus status, LocalDateTime startTime, LocalDateTime endTime);

    boolean existsByName(String flashSaleName);

    @Query("SELECT f FROM FlashSale f WHERE f.status = :status AND f.startTime <= :currentTime")
    List<FlashSale> findInactiveToActivate(FlashSaleStatus status, LocalDateTime currentTime);

    @Query("SELECT f FROM FlashSale f WHERE f.status = :status AND f.endTime <= :currentTime")
    List<FlashSale> findActiveToExpire(FlashSaleStatus status, LocalDateTime currentTime);

    @Modifying
    @Query("UPDATE FlashSale f SET f.status = :status WHERE f.id IN :ids")
    void updateStatusByIds(List<Long> ids, FlashSaleStatus status);
}
