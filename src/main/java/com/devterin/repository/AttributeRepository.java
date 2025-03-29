package com.devterin.repository;

import com.devterin.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    boolean existsByValue(String value);
    List<Attribute> findByAttributeTypeId(Long attributeTypeId);


}
