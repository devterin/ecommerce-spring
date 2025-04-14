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
import com.devterin.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final VariantRepository variantRepository;
    private final CartMapper cartMapper;

    @Override
    public List<CartResponse> getAllCarts() {
        log.info("Fetching all carts");
        List<Cart> carts = cartRepository.findAll();

        return carts.stream().map(cartMapper::toDto).toList();
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Cart not found for user id: " + userId));
        log.info("Fetching cart with for userId: {}", userId);
        // check xem giỏ hàng có thuộc về user này không
        if (!cart.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cart does not belong to the user");
        }
        return cartMapper.toDto(cart);
    }

    @Override
    public CartResponse addProductToCart(Long userId, CartRequest request) {
        Cart cart = findOrCreateCart(userId);
        Variant variant = validateVariantStock(request.getVariantId(), request.getQuantity());
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

    @Override
    public CartResponse updateCartItem(Long userId, CartRequest request) {
        Cart cart = findOrCreateCart(userId);
        validateVariantStock(request.getVariantId(), request.getQuantity());
        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), request.getVariantId());
        if (cartItem == null) {
            throw new EntityNotFoundException("Product not found in cart");
        }
        if (request.getQuantity() <= 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItem.setTotalPrice(cartItem.getQuantity() * cartItem.getUnitPrice());
        }
        cart.setTotalPrice(cart.getCartItems().stream().mapToInt(CartItem::getTotalPrice).sum());

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public CartResponse removeProductFromCart(Long userId, Long variantId) {
        Cart cart = findOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId);
        if (cartItem == null) {
            throw new EntityNotFoundException("Product not found in cart");
        }
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cart.setTotalPrice(cart.getCartItems().stream().mapToInt(CartItem::getTotalPrice).sum());
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user id: " + userId));

        cartItemRepository.deleteAll(cart.getCartItems());

        cart.getCartItems().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }

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

    private Variant validateVariantStock(Long variantId, int quantity) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Variant not found"));

        if (variant.getStockQuantity() < quantity) {
            throw new RuntimeException("The variant " + variant.getProduct().getName() + " is out of stock");
        }
        return variant;
    }

}
