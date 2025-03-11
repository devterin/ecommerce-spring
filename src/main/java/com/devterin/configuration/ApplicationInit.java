package com.devterin.configuration;

import com.devterin.entity.Role;
import com.devterin.entity.User;
import com.devterin.repository.RoleRepository;
import com.devterin.repository.UserRepository;
import com.devterin.utils.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationInit {
    private final RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role roles = Role.builder()
                        .name(RoleType.ADMIN.name())
                        .build();
                roleRepository.save(roles);

                User user = User.builder()
                        .username("admin")
                        .password("admin")
                        .active(false)
                        .roles(Set.of(roles))
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin");
            }
        };
    }

}
