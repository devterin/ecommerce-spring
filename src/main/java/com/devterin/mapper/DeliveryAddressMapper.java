package com.devterin.mapper;

import com.devterin.dtos.response.DeliveryAddressResponse;
import com.devterin.entity.DeliveryAddress;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressMapper {
    public DeliveryAddressResponse toResponse(DeliveryAddress address) {
        return DeliveryAddressResponse.builder()
                .id(address.getId())
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .phoneNumber(address.getPhoneNumber())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .address(address.getAddress())
                .isDefault(address.isDefault())
                .build();
    }
}
