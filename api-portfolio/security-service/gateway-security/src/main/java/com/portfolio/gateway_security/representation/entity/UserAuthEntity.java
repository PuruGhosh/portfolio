package com.portfolio.gateway_security.representation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "userAuth")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthEntity {
    @Id UUID id;
    UUID userId;
    String username;
    String password;
    List<String> roles;
    LocalDateTime createdAt;
    LocalDateTime modifiedAt;

    public UserAuthEntity(UUID userId, String username, String password, List<String> roles){
        setId(UUID.randomUUID());
        setUserId(userId);
        setPassword(password);
        setUsername(username);
        setRoles(roles);
        setCreatedAt(LocalDateTime.now());
        setModifiedAt(getCreatedAt());
    }

    public void setPassword(String password) {
        this.password = new BCryptPasswordEncoder().encode(password);
    }
}
