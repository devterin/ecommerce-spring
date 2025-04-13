package com.devterin.service.impl;

import com.devterin.dtos.request.OrderRequest;
import com.devterin.dtos.response.OrderResponse;
import com.devterin.entity.*;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import com.devterin.mapper.OrderMapper;
import com.devterin.repository.*;
import com.devterin.service.OrderService;
import com.devterin.utils.OrderStatus;
import com.devterin.utils.PaymentMethod;
import com.devterin.utils.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final VariantRepository variantRepository;
    private final OrderMapper orderMapper;


    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponse> getAllOrders(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Order> list = orderRepository.findAll(pageable).getContent();
        return list.stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrderByUserId(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(orderMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderUserByOrderId(Long orderId, Long userId) {
//        Order orders = orderRepository.findByIdAndUserId(orderId, userId)
//                .orElseThrow(() -> new EntityNotFoundException("Order not found or access denied"));
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to view this order");
        }

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled order");
        }

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderDetails(Long orderId, Long userId, OrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId))
            throw new RuntimeException("You are not authorized to view this order");

        if (order.getOrderStatus() != OrderStatus.PENDING)
            throw new IllegalStateException("Order details can only be updated in PENDING status.");

        order.setAddress(request.getAddress());
        order.setNote(request.getNote());
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You are not authorized to cancel this order.");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order cannot be cancelled in " + order.getOrderStatus() + " status.");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        Payment payment = order.getPayment();
        payment.setPaymentStatus(PaymentStatus.FAILED);

        rollbackStock(order.getOrderItems());

        paymentRepository.save(payment);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }
    @Override
    @Transactional
    public OrderResponse confirmOrderDeliveryByCOD(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        // check roles
        if (!order.getUser().getId().equals(userId)) {
            throw new SecurityException("You do not have permission to confirm this order");
        }
        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Order is not in SHIPPED status. Cannot confirm delivery.");
        }
        // check payment method (chỉ áp dụng COD)
        Payment payment = order.getPayment();
        if (payment.getPaymentMethod() != PaymentMethod.COD) {
            throw new IllegalStateException("This method is only applicable for COD orders.");
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        paymentRepository.save(payment);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    private void rollbackStock(List<OrderItem> orderItems) {
        List<Variant> variantsToUpdate = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Variant variant = item.getVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            variant.setSoldQuantity(variant.getSoldQuantity() - item.getQuantity());
            variantsToUpdate.add(variant);
        }
        variantRepository.saveAll(variantsToUpdate);
    }

    private List<Variant> checkAndUpdateStock(List<CartItem> cartItems) {
        List<Variant> variantsToUpdate = new ArrayList<>();
        // check stock quantity
        for (CartItem item : cartItems) {
            Variant variant = item.getVariant();
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for variant: " + variant.getProduct().getName());
            }
            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            variant.setSoldQuantity(variant.getSoldQuantity() + item.getQuantity());
            variantsToUpdate.add(variant);
        }
        return variantsToUpdate;
    }
}
