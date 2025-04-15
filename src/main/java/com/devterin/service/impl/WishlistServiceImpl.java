package com.devterin.service.impl;

import com.devterin.dtos.request.WishlistRequest;
import com.devterin.dtos.response.WishlistResponse;
import com.devterin.entity.User;
import com.devterin.entity.Variant;
import com.devterin.entity.Wishlist;
import com.devterin.exception.ErrorCode;
import com.devterin.mapper.WishlistMapper;
import com.devterin.repository.UserRepository;
import com.devterin.repository.VariantRepository;
import com.devterin.repository.WishlistRepository;
import com.devterin.service.WishlistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final VariantRepository variantRepository;
    private final WishlistMapper wishlistMapper;

    @Override
    public WishlistResponse addProductToWishlist(WishlistRequest request) {
        Long userId = getCurrentUserId();
        Long variantId = request.getVariantId();

        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));

        if (wishlistRepository.existsByUserIdAndVariantId(userId, variantId)) {
            throw new IllegalArgumentException("Wishlist existed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .variant(variant)
                .build();
        wishlistRepository.save(wishlist);
        log.info("Added variant {} to wishlist", variant.getName());

        return wishlistMapper.toDto(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long variantId) {
        Long userId = getCurrentUserId();

        Wishlist wishlist = wishlistRepository.findByUserIdAndVariantId(userId, variantId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist not found"));

        wishlistRepository.delete(wishlist);
        log.info("Removed product {} from wishlist", variantId);
    }

    @Override
    public List<WishlistResponse> myWishlist() {
        Long userId = getCurrentUserId();
        List<Wishlist> wishlist = wishlistRepository.findWishlistByUserId(userId);
        if (wishlist.isEmpty()) {
            throw new IllegalArgumentException("Wishlist not found");
        }

        return wishlist.stream().map(wishlistMapper::toDto).collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage())).getId();
    }

}
