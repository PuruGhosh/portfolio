package com.portfolio.userservice.entity.dto;

import com.portfolio.userservice.entity.representation.SkillReference;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private UUID id;
  private String name;
  private String email;
  private String bio;
  private String role;
  private List<UUID> projects;
  private List<SkillReference> skills;
}
