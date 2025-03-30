package com.devterin.repository;

import com.devterin.entity.Cart;
import com.devterin.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndVariantId(Long cartId, Long variantId);
}
