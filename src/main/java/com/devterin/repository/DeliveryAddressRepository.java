package com.devterin.repository;

import com.devterin.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    @Query("SELECT address FROM DeliveryAddress address WHERE address.user.id = :userId ORDER BY address.isDefault DESC, address.id ASC")
    List<DeliveryAddress> findAllForUser(@Param("userId") Long userId);

    Optional<DeliveryAddress> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndIsDefaultTrue(Long userId);
}
