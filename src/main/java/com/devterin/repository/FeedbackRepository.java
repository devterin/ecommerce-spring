package com.devterin.repository;

import com.devterin.entity.Attribute;
import com.devterin.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    boolean existsByOrderIdAndUserId(Long orderId, Long userId);

    Feedback findByOrderIdAndUserId(Long orderId, Long userId);
}
