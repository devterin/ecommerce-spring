package com.devterin.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "attributes")
public class Attribute extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attribute_id")
    private Long id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_type_id")
    @JsonBackReference
    private AttributeType attributeType;
}
