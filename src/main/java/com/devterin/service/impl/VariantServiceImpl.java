package com.devterin.service.impl;

import com.devterin.repository.VariantRepository;
import com.devterin.service.ProductService;
import com.devterin.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {

    private final ProductService productService;
    private final VariantRepository variantRepository;


}
