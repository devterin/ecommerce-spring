package com.devterin.service.impl;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.request.VariantRequest;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.dtos.response.UserResponse;
import com.devterin.entity.*;
import com.devterin.mapper.OrderMapper;
import com.devterin.repository.*;
import com.devterin.utils.OrderStatus;
import com.devterin.utils.PaymentMethod;
import com.devterin.utils.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final VariantRepository variantRepository;
    private final OrderMapper orderMapper;

    private List<Variant> checkAndUpdateStock(List<CartItem> cartItems) {
        List<Variant> variantsToUpdate = new ArrayList<>();
        // Kiểm tra tồn kho
        for (CartItem item : cartItems) {
            Variant variant = item.getVariant();
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for variant: " + variant.getProduct().getName());
            }
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            variantsToUpdate.add(variant);
        }
        return variantsToUpdate;
    }


    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot create order.");
        }
        List<Variant> checkAndUpdateStock = checkAndUpdateStock(cart.getCartItems());

        Order order = Order.builder()
                .user(user)
                .totalAmount(cart.getTotalPrice())
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .note(request.getNote())
                .address(request.getAddress())
                .build();

        Payment payment = Payment.builder()
                .amount(order.getTotalAmount())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(PaymentStatus.PENDING)
                .order(order)
                .build();
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        variantRepository.saveAll(checkAndUpdateStock);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .variant(cartItem.getVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
            orderItems.add(orderItem);
        }
        
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);
        cartRepository.delete(cart);

        return orderMapper.toDto(order);
    }

}
