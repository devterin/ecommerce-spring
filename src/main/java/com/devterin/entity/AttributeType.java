package com.devterin.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "attribute_types")
@NoArgsConstructor
@AllArgsConstructor
public class AttributeType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "attributeType", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attribute> attributes;
}
