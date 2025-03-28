package com.devterin.repository;

import com.devterin.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    boolean existsByValue(String value);
    List<AttributeValue> findByAttributeTypeId(Long attributeTypeId);


}
