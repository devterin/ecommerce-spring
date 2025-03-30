package com.devterin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "variants")
public class Variant extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Long id;

    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity")
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany
    @JoinTable(name = "variant_attribute",
            joinColumns = @JoinColumn(name = "variant_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id"))
    private List<Attribute> attributes = new ArrayList<>();

}
