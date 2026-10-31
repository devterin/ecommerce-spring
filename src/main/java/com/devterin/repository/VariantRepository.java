package com.devterin.repository;

import com.devterin.entity.Variant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface VariantRepository extends JpaRepository<Variant, Long> {
    boolean existsByNameAndProductId(String variantName, Long productId);
    boolean existsByNameAndProductIdAndIdNot(String variantName, Long productId, Long variantId);

    int countById(Long variantId);
    List<Variant> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT variant FROM Variant variant WHERE variant.id = :variantId")
    Optional<Variant> findByIdForUpdate(@Param("variantId") Long variantId);
}
