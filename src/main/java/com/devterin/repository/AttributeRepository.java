package com.devterin.repository;

import com.devterin.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;


public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    boolean existsByValue(String value);
    Attribute findByValue(String value);
    Attribute findByValueAndAttributeTypeId(String value, Long attributeTypeId);
    List<Attribute> findByAttributeTypeId(Long attributeTypeId);

    @Query("""
            SELECT DISTINCT a.value
            FROM Attribute a
            JOIN a.attributeType type
            WHERE LOWER(type.name) LIKE '%size%'
            ORDER BY a.value
            """)
    List<String> findDistinctSizeValues();

    @Query("""
            SELECT DISTINCT a.value
            FROM Attribute a
            JOIN a.attributeType type
            WHERE LOWER(type.name) LIKE '%color%'
            ORDER BY a.value
            """)
    List<String> findDistinctColorValues();

}
