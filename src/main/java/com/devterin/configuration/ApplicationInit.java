package com.devterin.configuration;

import com.devterin.entity.Role;
import com.devterin.entity.User;
import com.devterin.repository.RoleRepository;
import com.devterin.repository.UserRepository;
import com.devterin.enums.TypeUser;
import com.devterin.ultil.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationInit {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role roles = Role.builder()
                        .name(TypeUser.ADMIN.name())
                        .build();
                roleRepository.save(roles);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .avatar(AppConstants.URL_DEFAULT_AVATAR)
                        .active(true)
                        .roles(Set.of(roles))
                        .build();
                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin");
            }
        };
    }

}
