package com.portfolio.userservice.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portfolio.userservice.entity.representation.SkillReference;
import java.util.List;
import java.util.UUID;

import com.portfolio.userservice.entity.representation.Tags;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonIgnoreProperties
public class UserResponseDto extends GetUserDto{
  private UUID id;
  private List<SkillReference> skills;
  private List<UUID> projectIds;
}
