package com.devterin.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "attribute_types")
public class AttributeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attribute_type_id")
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "attributeType", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attribute> attributes;
}
