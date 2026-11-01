package com.devterin.repository;

import com.devterin.dtos.dto.ProductRatingView;
import com.devterin.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByOrderIdAndUserId(Long orderId, Long userId);

    boolean existsByOrderIdAndUserIdAndProductId(Long orderId, Long userId, Long productId);

    Feedback findByOrderIdAndUserId(Long orderId, Long userId);

    Feedback findByOrderIdAndUserIdAndProductId(Long orderId, Long userId, Long productId);

    List<Feedback> findAllByOrderIdAndUserIdOrderByCreatedAtDesc(Long orderId, Long userId);

    List<Feedback> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            SELECT DISTINCT f
            FROM Feedback f
            LEFT JOIN f.product p
            LEFT JOIN f.order o
            LEFT JOIN o.orderItems oi
            WHERE p.id = :productId
               OR (p IS NULL AND oi.variant.product.id = :productId)
            ORDER BY f.createdAt DESC
            """)
    List<Feedback> findAllProductFeedback(@Param("productId") Long productId);

    @Query("""
            SELECT f.product.id AS productId,
                   AVG(f.rating) AS averageRating,
                   COUNT(f.id) AS reviewCount
            FROM Feedback f
            WHERE f.product.id IN :productIds
            GROUP BY f.product.id
            """)
    List<ProductRatingView> aggregateRatingsByProductIds(@Param("productIds") Collection<Long> productIds);

}
