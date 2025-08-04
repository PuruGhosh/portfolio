package com.portfolio.userservice.mappers;

import com.portfolio.userservice.entity.User;
import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.UserDto;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class UserMapper {
  private static final String NOT_UPDATED = "N/A";

  public UserDto fromUserEntity(User user) {
    Assert.notNull(user, "Null Arguments received");
    return UserDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole())
        .bio(user.getBio())
        .skills(user.getSkillIds())
        .projects(user.getProjectIds())
        .build();
  }

  public User fromUserDto(UserDto dto) {
    Assert.notNull(dto, "Null arguments received");

    return User.builder()
        .id(dto.getId())
        .name(dto.getName())
        .email(dto.getEmail())
        .role(dto.getRole())
        .bio(dto.getBio())
        .skillIds(dto.getSkills())
        .projectIds(dto.getProjects())
        .build();
  }

  public User fromGetUserDto(GetUserDto dto) {
    Assert.notNull(dto, "Null arguments received");

    return User.builder()
        .id(UUID.randomUUID())
        .name(dto.getName())
        .email(dto.getEmail())
        .username(dto.getUserName())
        .password(dto.getPassword())
        .role(dto.getRole() == null ? NOT_UPDATED : dto.getRole())
        .bio(NOT_UPDATED)
        .skillIds(null)
        .projectIds(null)
        .build();
  }
}
