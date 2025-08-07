package com.portfolio.userservice.repository;

import com.portfolio.userservice.entity.User;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
  boolean existsByEmail(String email);
}
