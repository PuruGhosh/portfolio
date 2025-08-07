package com.portfolio.userservice.entity;

import com.portfolio.userservice.entity.representation.SkillReference;
import com.portfolio.userservice.entity.representation.Tags;
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
    private String bio;
    private List<String> roles;
    private List<UUID> projectIds;
    private List<SkillReference> skillIds;
    Tags tags;
}
