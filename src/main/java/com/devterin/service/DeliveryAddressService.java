package com.devterin.service;

import com.devterin.dtos.request.DeliveryAddressRequest;
import com.devterin.dtos.response.DeliveryAddressResponse;
import com.devterin.entity.DeliveryAddress;
import com.devterin.entity.User;
import com.devterin.mapper.DeliveryAddressMapper;
import com.devterin.repository.DeliveryAddressRepository;
import com.devterin.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressMapper addressMapper;

    @Transactional(readOnly = true)
    public List<DeliveryAddressResponse> getMyAddresses(Long userId) {
        return addressRepository.findAllForUser(userId).stream().map(addressMapper::toResponse).toList();
    }

    @Transactional
    public DeliveryAddressResponse create(Long userId, DeliveryAddressRequest request) {
        User user = getUser(userId);
        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault()) || !addressRepository.existsByUser_IdAndIsDefaultTrue(userId);
        if (makeDefault) clearDefaults(userId);

        DeliveryAddress address = DeliveryAddress.builder()
                .user(user)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phoneNumber(request.getPhoneNumber().trim())
                .postalCode(request.getPostalCode().trim())
                .city(request.getCity().trim())
                .address(request.getAddress().trim())
                .isDefault(makeDefault)
                .build();
        address = addressRepository.save(address);
        if (makeDefault) syncProfileAddress(user, address);
        return addressMapper.toResponse(address);
    }

    @Transactional
    public DeliveryAddressResponse update(Long userId, Long addressId, DeliveryAddressRequest request) {
        User user = getUser(userId);
        DeliveryAddress address = getOwnedAddress(userId, addressId);
        boolean wasDefault = address.isDefault();
        copyRequest(address, request);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaults(userId);
            address.setDefault(true);
        } else if (wasDefault) {
            address.setDefault(false);
        }

        address = addressRepository.save(address);
        if (!address.isDefault() && !addressRepository.existsByUser_IdAndIsDefaultTrue(userId)) {
            List<DeliveryAddress> addresses = addressRepository.findAllForUser(userId);
            DeliveryAddress nextDefault = addresses.stream().filter(item -> !item.getId().equals(addressId)).findFirst().orElse(address);
            nextDefault.setDefault(true);
            addressRepository.save(nextDefault);
            address = addressRepository.findByIdAndUser_Id(addressId, userId).orElseThrow();
        }
        DeliveryAddress defaultAddress = addressRepository.findAllForUser(userId).stream()
                .filter(DeliveryAddress::isDefault).findFirst().orElse(address);
        syncProfileAddress(user, defaultAddress);
        return addressMapper.toResponse(address);
    }

    @Transactional
    public DeliveryAddressResponse setDefault(Long userId, Long addressId) {
        User user = getUser(userId);
        DeliveryAddress address = getOwnedAddress(userId, addressId);
        clearDefaults(userId);
        address.setDefault(true);
        addressRepository.save(address);
        syncProfileAddress(user, address);
        return addressMapper.toResponse(address);
    }

    @Transactional
    public void delete(Long userId, Long addressId) {
        User user = getUser(userId);
        DeliveryAddress address = getOwnedAddress(userId, addressId);
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        List<DeliveryAddress> remaining = addressRepository.findAllForUser(userId);
        if (remaining.isEmpty()) {
            user.setAddress(null);
            userRepository.save(user);
            return;
        }
        DeliveryAddress defaultAddress = remaining.stream().filter(DeliveryAddress::isDefault).findFirst().orElse(remaining.get(0));
        if (wasDefault || !defaultAddress.isDefault()) {
            defaultAddress.setDefault(true);
            addressRepository.save(defaultAddress);
        }
        syncProfileAddress(user, defaultAddress);
    }

    private void clearDefaults(Long userId) {
        addressRepository.findAllForUser(userId).stream()
                .filter(DeliveryAddress::isDefault)
                .forEach(address -> address.setDefault(false));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private DeliveryAddress getOwnedAddress(Long userId, Long addressId) {
        return addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery address not found"));
    }

    private void copyRequest(DeliveryAddress address, DeliveryAddressRequest request) {
        address.setFirstName(request.getFirstName().trim());
        address.setLastName(request.getLastName().trim());
        address.setPhoneNumber(request.getPhoneNumber().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCity(request.getCity().trim());
        address.setAddress(request.getAddress().trim());
    }

    private void syncProfileAddress(User user, DeliveryAddress address) {
        user.setAddress(String.join("\n", address.getAddress(), address.getCity(), address.getPostalCode()));
        userRepository.save(user);
    }
}
