package com.devterin.entity;

import jakarta.persistence.*;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    private Integer quantity;

    private Integer unitPrice;

    private Integer totalPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;

    @ManyToOne
    @JoinColumn(name = "flash_sale_item_id")
    private FlashSaleItem flashSaleItem;
}
