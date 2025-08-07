package com.portfolio.userservice.mappers;

import com.portfolio.userservice.entity.User;
import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.UserResponseDto;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import com.portfolio.userservice.entity.representation.Tags;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UserMapper {
  private static final String NOT_UPDATED = "N/A";

  public UserResponseDto fromUserEntity(User user) {
    Assert.notNull(user, "Null Arguments received");
    return UserResponseDto.builder()
        .id(user.getId())
        .name(user.getName())
        .roles(user.getRoles())
        .email(user.getEmail())
        .bio(user.getBio())
        .skills(user.getSkillIds())
        .projectIds(user.getProjectIds())
        .skillTags(user.getTags().getSkillTags())
        .projectTags(user.getTags().getProjectTags())
        .build();
  }

  public User fromUserDto(UserResponseDto dto) {
    Assert.notNull(dto, "Null arguments received");
    return User.builder()
        .id(dto.getId())
        .name(dto.getName())
        .email(dto.getEmail())
        .roles(dto.getRoles())
        .tags(
            Tags.builder()
                .skillTags(
                    Objects.isNull(dto.getSkillTags())
                        ? Collections.emptyList()
                        : dto.getSkillTags())
                .projectTags(
                    Objects.isNull(dto.getProjectTags())
                        ? Collections.emptyList()
                        : dto.getProjectTags())
                .build())
        .bio(dto.getBio())
        .skillIds(dto.getSkills())
        .projectIds(dto.getProjectIds())
        .build();
  }

  public User fromGetUserDto(GetUserDto dto) {
    Assert.notNull(dto, "Null arguments received");

    return User.builder()
        .id(UUID.randomUUID())
        .name(dto.getName())
        .email(dto.getEmail())
        .roles(dto.getRoles() == null ? Collections.emptyList() : dto.getRoles())
        .bio(dto.getBio().isEmpty() ? NOT_UPDATED : dto.getBio())
        .skillIds(null)
        .projectIds(null)
        .tags(
            Tags.builder()
                .skillTags(
                    Objects.isNull(dto.getSkillTags())
                        ? Collections.emptyList()
                        : dto.getSkillTags())
                .projectTags(
                    Objects.isNull(dto.getProjectTags())
                        ? Collections.emptyList()
                        : dto.getProjectTags())
                .build())
        .build();
  }
}
