package com.devterin.service.impl;

import com.devterin.dto.request.CreateUserRequest;
import com.devterin.dto.request.UpdateUserRequest;
import com.devterin.dto.response.UserResponse;
import com.devterin.entity.Role;
import com.devterin.entity.User;
import com.devterin.exception.AppException;
import com.devterin.exception.ErrorCode;
import com.devterin.mapper.UserMapper;
import com.devterin.repository.RoleRepository;
import com.devterin.repository.UserRepository;
import com.devterin.service.UserService;
import com.devterin.utils.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role roles = Role.builder()
                .name(RoleType.USER.name())
                .build();
        roleRepository.save(roles);

        User user = userMapper.toEntity(request);
        user.setPassword(request.getPassword());
        user.setRoles(Set.of(roles));
        user = userRepository.save(user);

        return userMapper.toDTO(user);
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toDTO).toList();
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());

        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

}
