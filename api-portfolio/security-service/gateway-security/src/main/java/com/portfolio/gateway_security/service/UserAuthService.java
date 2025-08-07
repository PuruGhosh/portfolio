package com.portfolio.gateway_security.service;

import com.portfolio.gateway_security.repository.UserAuthRepository;
import com.portfolio.gateway_security.representation.entity.UserAuthEntity;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class UserAuthService {

    private final UserAuthRepository userRepository;

    public UserAuthService(UserAuthRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserAuthEntity> register(UserAuthEntity user) {
        Objects.requireNonNull(user, "UserAuthEntity must not be null");
        Objects.requireNonNull(user.getUsername(), "Username must not be null");
        Objects.requireNonNull(user.getPassword(), "Password must not be null");
        return userRepository.save(user);
    }

    public Mono<UserAuthEntity> getByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return Mono.error(new IllegalArgumentException("Username must not be empty"));
        }
        return userRepository.findByUsername(username);
    }

    public Mono<UserAuthEntity> getByUserId(UUID userId) {
        if (!Objects.nonNull(userId)) {
            return Mono.error(new IllegalArgumentException("UserAuthEntity ID must not be empty"));
        }
        return userRepository.findByUserId(userId);
    }

    public Mono<Boolean> userExists(String username) {
        if (!StringUtils.hasText(username)) {
            return Mono.just(false);
        }
        return userRepository.findByUsername(username)
                .map(user -> true)
                .defaultIfEmpty(false);
    }
}
