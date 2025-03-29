package com.devterin.repository;

import com.devterin.entity.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AttributeTypeRepository extends JpaRepository<AttributeType, Long> {
    boolean existsByName(String value);

}
