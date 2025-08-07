package com.portfolio.gateway_security.repository;

import com.portfolio.gateway_security.representation.entity.UserAuthEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserAuthRepository extends ReactiveMongoRepository<UserAuthEntity, UUID> {
    Mono<UserAuthEntity> findByUserId(UUID userId);
    Mono<UserAuthEntity> findByUsername(String username);
}
