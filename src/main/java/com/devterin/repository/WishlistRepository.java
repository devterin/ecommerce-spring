package com.devterin.repository;

import com.devterin.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    boolean existsByUserIdAndVariantId(Long userId, Long variantId);

    Optional<Wishlist> findByUserIdAndVariantId(Long userId, Long variantId);

    List<Wishlist> findWishlistByUserId(Long userId);
}
