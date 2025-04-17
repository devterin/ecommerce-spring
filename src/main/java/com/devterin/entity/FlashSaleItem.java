package com.devterin.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "flash_sale_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @Column(name = "sale_price", nullable = false)
    private Integer salePrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "sold_quantity")
    private Integer soldQuantity;
}
