package com.devterin.service.impl;

import com.devterin.dtos.request.CartRequest;
import com.devterin.dtos.response.CartResponse;
import com.devterin.entity.Cart;
import com.devterin.entity.CartItem;
import com.devterin.entity.User;
import com.devterin.entity.Variant;
import com.devterin.mapper.CartMapper;
import com.devterin.repository.CartItemRepository;
import com.devterin.repository.CartRepository;
import com.devterin.repository.UserRepository;
import com.devterin.repository.VariantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final VariantRepository variantRepository;
    private final CartMapper cartMapper;

    private Cart findOrCreateCart(Long userId) {

        return cartRepository.findByUserId(userId).orElseGet(
                () -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found"));
                    Cart newCart = Cart.builder()
                            .user(user)
                            .totalPrice(0)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public CartResponse addProductToCart(Long userId, CartRequest request) {

        Cart cart = findOrCreateCart(userId);

        Variant variant = variantRepository.findById(request.getVariantId()).orElseThrow(
                () -> new EntityNotFoundException("Variant not found"));
        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("The variant " + variant.getProduct().getName() + " is out of stock");
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), request.getVariantId());

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setTotalPrice(cartItem.getQuantity() * cartItem.getUnitPrice());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .quantity(request.getQuantity())
                    .variant(variant)
                    .unitPrice(variant.getPrice())
                    .totalPrice(request.getQuantity() * variant.getPrice())
                    .build();
            cart.getCartItems().add(cartItem);
        }
        cart.setTotalPrice(cart.getCartItems().stream().mapToInt(CartItem::getTotalPrice).sum());
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }
}
