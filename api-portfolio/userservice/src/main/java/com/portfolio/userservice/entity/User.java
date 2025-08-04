package com.portfolio.userservice.entity;

import com.portfolio.userservice.entity.representation.SkillReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private UUID id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String bio;
    private String role;
    private List<UUID> projectIds;
    private List<SkillReference> skillIds;
    List<String> tags;
}
