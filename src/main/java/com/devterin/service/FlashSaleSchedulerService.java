package com.devterin.service;

import com.devterin.entity.FlashSale;
import com.devterin.enums.FlashSaleStatus;
import com.devterin.repository.FlashSaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlashSaleSchedulerService {

    private final FlashSaleRepository flashSaleRepository;

    @Scheduled(fixedRate = 60_000) // Chạy mỗi phút
    @Transactional
    public void updateFlashSaleStatus() {
        LocalDateTime now = LocalDateTime.now();
        activateFlashSales(now);

        expireFlashSales(now);
    }

    private void activateFlashSales(LocalDateTime now) {
        List<FlashSale> inactiveSales = flashSaleRepository.findInactiveToActivate(FlashSaleStatus.INACTIVE, now);
        if (!inactiveSales.isEmpty()) {
            List<Long> ids = inactiveSales.stream().map(FlashSale::getId).collect(Collectors.toList());
            flashSaleRepository.updateStatusByIds(ids, FlashSaleStatus.ACTIVE);
            log.info("Activated {} flash sales: {}", ids.size(), ids);
        }
    }

    private void expireFlashSales(LocalDateTime now) {
        List<FlashSale> activeSales = flashSaleRepository.findActiveToExpire(FlashSaleStatus.ACTIVE, now);
        if (!activeSales.isEmpty()) {
            List<Long> ids = activeSales.stream().map(FlashSale::getId).collect(Collectors.toList());
            flashSaleRepository.updateStatusByIds(ids, FlashSaleStatus.EXPIRED);
            log.info("Expired {} flash sales: {}", ids.size(), ids);
        }
    }
}
