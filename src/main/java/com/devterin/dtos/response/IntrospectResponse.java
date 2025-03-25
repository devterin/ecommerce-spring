package com.devterin.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntrospectResponse {
    boolean valid;
}
