package com.devterin.dtos.request;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackRequest {

    private Integer rating;
    private String comment;
}
